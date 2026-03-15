import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../services/api.service';
import { EducationContent } from '../../../models/models';

@Component({
  selector: 'app-education-center',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './education-center.html',
  styleUrls: ['./education-center.scss']
})
export class EducationCenterComponent implements OnInit {
  languages = [
    { code: 'en', label: 'English' },
    { code: 'hi', label: 'Hindi' },
    { code: 'te', label: 'Telugu' },
    { code: 'es', label: 'Spanish' },
    { code: 'kn', label: 'Kannada' }
  ];

  selectedLanguageCode = 'en';
  contents: EducationContent[] = [];
  selectedTopic: EducationContent | null = null;
  loading = false;
  
  // Track currently playing fallback audio so we can stop it
  currentAudio: HTMLAudioElement | null = null;
  isPlaying = false;

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.loadContent();
    // Pre-load voices for Chrome
    if (window.speechSynthesis) {
        window.speechSynthesis.getVoices();
        window.speechSynthesis.onvoiceschanged = () => {
            window.speechSynthesis.getVoices();
        };
    }
  }

  onLanguageChange() {
    this.stopAudio();
    this.selectedTopic = null;
    this.loadContent();
  }

  stopAudio() {
    this.isPlaying = false;
    if (this.currentAudio) {
      this.currentAudio.pause();
      this.currentAudio = null;
    }
    window.speechSynthesis?.cancel();
  }

  openLesson(content: EducationContent) {
    this.selectedTopic = content;
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  closeLesson() {
    this.stopAudio();
    this.selectedTopic = null;
  }

  async translateText(text: string, targetLang: string): Promise<string> {
    if (targetLang === 'en') return text;
    try {
      const url = `https://translate.googleapis.com/translate_a/single?client=gtx&sl=en&tl=${targetLang}&dt=t&q=${encodeURIComponent(text)}`;
      const res = await fetch(url);
      const data = await res.json();
      return data[0]?.map((item: any) => item[0])?.join('') || text;
    } catch (err) {
      console.error('Translation API error:', err);
      return text;   
    }
  }

  loadContent() {
    this.loading = true;
    
    // Always fetch 'en' content from backend so we get all 6 lessons dynamically mapped
    this.api.getEducationContentByLanguage('en').subscribe({
      next: async (data) => {
        if (this.selectedLanguageCode === 'en') {
          this.contents = data;
          this.loading = false;
          return;
        }

        try {
          // Translate all data live using the Google Translate Free API mapping
          const translatedContents = await Promise.all(
            data.map(async (item) => {
              const transTitle = await this.translateText(item.title, this.selectedLanguageCode);
              const transContent = await this.translateText(item.content, this.selectedLanguageCode);
              return {
                ...item,
                title: transTitle,
                content: transContent,
                language: this.selectedLanguageCode
              };
            })
          );
          this.contents = translatedContents;
        } catch (err) {
          console.error('Translation failed', err);
          this.contents = data; // Fallback
        } finally {
          this.loading = false;
        }
      },
      error: (err) => {
        console.error('Failed to load education content', err);
        this.loading = false;
      }
    });
  }

  async listenToLesson(textContent: string) {
    // 1) Stop anything currently playing
    this.stopAudio();

    const voices = window.speechSynthesis?.getVoices() || [];
    const hasNativeVoice = voices.some(v => v.lang.startsWith(this.selectedLanguageCode));

    // 2) If it's English or OS explicitly has a voice package for this language, use SpeechSynthesis
    if (window.speechSynthesis && (hasNativeVoice || this.selectedLanguageCode === 'en' || this.selectedLanguageCode === 'es')) {
      const speech = new SpeechSynthesisUtterance(textContent);
      
      const langMap: { [key: string]: string } = {
        'en': 'en-US',
        'hi': 'hi-IN',
        'te': 'te-IN',
        'es': 'es-ES',
        'kn': 'kn-IN'
      };
      const targetLang = langMap[this.selectedLanguageCode] || 'en-US';
      speech.lang = targetLang;
      
      const exactVoice = voices.find(v => v.lang === targetLang || v.lang.startsWith(this.selectedLanguageCode));
      if (exactVoice) {
         speech.voice = exactVoice;
      }
      
      speech.onend = () => this.isPlaying = false;
      
      window.speechSynthesis.speak(speech);
      this.isPlaying = true;
      return;
    }

    // 3) Windows Desktop often natively lacks Te/Kn voices. Use pure HTTP audio buffer payload stream as fallback.
    try {
      // Free TTS restricts char limits per request to 200 items. Break down chunks properly.
      const chunks: string[] = [];
      const splitByNewlines = textContent.split('\n');
      for (const block of splitByNewlines) {
        if (!block.trim()) continue;
        const sentences = block.replace(/([.?!।])\s+/g, "$1|").split("|");
        for (let s of sentences) {
           s = s.trim();
           if (s.length > 200) s = s.substring(0, 197) + '...'; // Truncate safety buffer
           if (s) chunks.push(s);
        }
      }

      const playSequentially = async (index: number) => {
        if (index >= chunks.length) return;
        
        const chunk = chunks[index];
        // Stream text through our secure backend proxy to bypass browser restrictions
        try {
          const blob = await new Promise<Blob>((resolve, reject) => {
             this.api.getTtsAudio(chunk, this.selectedLanguageCode).subscribe({
                next: (b: Blob) => resolve(b),
                error: (e: any) => reject(e)
             });
          });
          
          if (!blob) {
            playSequentially(index + 1);
            return;
          }

          const objectUrl = URL.createObjectURL(blob);
          this.currentAudio = new Audio(objectUrl);
          
          this.currentAudio.onended = () => {
            URL.revokeObjectURL(objectUrl);
            playSequentially(index + 1);
          };
          this.currentAudio.onerror = (err) => {
            console.error("Audio chunk strictly failed", err);
            URL.revokeObjectURL(objectUrl);
            playSequentially(index + 1);
          };

          await this.currentAudio.play();
          this.isPlaying = true;
        } catch (err) {
            console.error("Audio fetch or play interrupted", err);
            console.warn("Attempting naive string fallback for chunk due to fetch failure");
            // Ultimate fallback to literal string reading if completely offline or backend fails
            const speech = new SpeechSynthesisUtterance(chunk);
            speech.lang = this.selectedLanguageCode === 'te' ? 'te-IN' : (this.selectedLanguageCode === 'kn' ? 'kn-IN' : 'en-US');
            window.speechSynthesis?.speak(speech);
            this.isPlaying = true;
            speech.onend = () => { 
                this.isPlaying = false;
                playSequentially(index + 1); 
            };
        }
      };
      
      await playSequentially(0);
    } catch (e) {
      console.error("Audio fallback playback error", e);
      alert("TTS audio playback failed in this browser for language: " + this.selectedLanguageCode);
      this.isPlaying = false;
    }
  }
}
