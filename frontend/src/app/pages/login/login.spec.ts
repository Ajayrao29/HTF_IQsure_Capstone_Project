/*
 * FILE: login.spec.ts
 * PURPOSE: Unit tests for LoginComponent.
 */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { LoginComponent } from './login';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { provideRouter } from '@angular/router';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;
  let apiMock: any;
  let authMock: any;
  let routerMock: any;

  beforeEach(async () => {
    apiMock = {
      login: jasmine.createSpy('login').and.returnValue(of({ userId: 1, name: 'Test' }))
    };
    authMock = {
      saveUser: jasmine.createSpy('saveUser')
    };
    routerMock = {
      navigate: jasmine.createSpy('navigate')
    };

    await TestBed.configureTestingModule({
      imports: [LoginComponent, FormsModule],
      providers: [
        { provide: ApiService, useValue: apiMock },
        { provide: AuthService, useValue: authMock },
        { provide: Router, useValue: routerMock },
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show error if fields are empty', () => {
    component.login();
    expect(component.error).toBe('Please fill in all fields');
    expect(apiMock.login).not.toHaveBeenCalled();
  });

  it('should show error for invalid email', () => {
    component.email = 'abc';
    component.password = '123';
    component.login();
    expect(component.error).toBe('Please enter a valid email');
  });

  it('should navigate to dashboard on successful login', fakeAsync(() => {
    component.email = 'test@example.com';
    component.password = 'pass123';
    
    component.login();
    tick();

    expect(apiMock.login).toHaveBeenCalledWith({ email: 'test@example.com', password: 'pass123' });
    expect(authMock.saveUser).toHaveBeenCalled();
    expect(routerMock.navigate).toHaveBeenCalledWith(['/dashboard']);
  }));

  it('should handle login error', fakeAsync(() => {
    component.email = 'fail@test.com';
    component.password = 'wrong';
    apiMock.login.and.returnValue(throwError(() => ({ status: 401, error: { message: 'Invalid credentials' } })));

    component.login();
    tick();

    expect(component.error).toBe('Invalid credentials');
    expect(component.loading).toBeFalse();
  }));
});
