/*
 * FILE: register.spec.ts
 * PURPOSE: Unit tests for RegisterComponent.
 */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RegisterComponent } from './register';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { provideRouter } from '@angular/router';

describe('RegisterComponent', () => {
  let component: RegisterComponent;
  let fixture: ComponentFixture<RegisterComponent>;
  let apiMock: any;
  let authMock: any;
  let routerMock: any;

  beforeEach(async () => {
    apiMock = {
      register: jasmine.createSpy('register').and.returnValue(of({ userId: 1 }))
    };
    authMock = {
      saveUser: jasmine.createSpy('saveUser')
    };
    routerMock = {
      navigate: jasmine.createSpy('navigate')
    };

    await TestBed.configureTestingModule({
      imports: [RegisterComponent, FormsModule],
      providers: [
        { provide: ApiService, useValue: apiMock },
        { provide: AuthService, useValue: authMock },
        { provide: Router, useValue: routerMock },
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(RegisterComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should validate password length', () => {
    component.name = 'John';
    component.email = 'john@test.com';
    component.password = '123';
    component.register();
    expect(component.error).toContain('at least 6 characters');
  });

  it('should call api.register and navigate on success', fakeAsync(() => {
    component.name = 'John Doe';
    component.email = 'john@test.com';
    component.password = 'password123';
    
    component.register();
    tick();

    expect(apiMock.register).toHaveBeenCalled();
    expect(authMock.saveUser).toHaveBeenCalled();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/dashboard']);
  }));
});
