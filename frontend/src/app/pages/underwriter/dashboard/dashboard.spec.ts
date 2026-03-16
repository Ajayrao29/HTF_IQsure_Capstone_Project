/*
 * FILE: dashboard.spec.ts
 * PURPOSE: Unit tests for UnderwriterDashboardComponent.
 */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { UnderwriterDashboardComponent } from './dashboard';
import { ApiService } from '../../../services/api.service';
import { AuthService } from '../../../services/auth.service';
import { of } from 'rxjs';
import { provideRouter } from '@angular/router';

describe('UnderwriterDashboardComponent', () => {
  let component: UnderwriterDashboardComponent;
  let fixture: ComponentFixture<UnderwriterDashboardComponent>;
  let apiMock: any;
  let authMock: any;

  beforeEach(async () => {
    apiMock = {
      getUnderwriterStats: jasmine.createSpy('getUnderwriterStats').and.returnValue(of({ quotesSent: 10 }))
    };
    authMock = {
      getUser: jasmine.createSpy('getUser').and.returnValue({ name: 'Agent X', licenseNumber: 'L123' }),
      getUserId: jasmine.createSpy('getUserId').and.returnValue(1)
    };

    await TestBed.configureTestingModule({
      imports: [UnderwriterDashboardComponent],
      providers: [
        { provide: ApiService, useValue: apiMock },
        { provide: AuthService, useValue: authMock },
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(UnderwriterDashboardComponent);
    component = fixture.componentInstance;
  });

  it('should initialize with user info and stats', () => {
    fixture.detectChanges();

    expect(component.userName).toBe('Agent X');
    expect(apiMock.getUnderwriterStats).toHaveBeenCalledWith(1);
    expect(component.stats.quotesSent).toBe(10);
    expect(component.loading).toBeFalse();
  });
});
