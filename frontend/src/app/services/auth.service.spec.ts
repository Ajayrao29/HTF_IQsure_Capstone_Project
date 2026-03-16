/*
 * FILE: auth.service.spec.ts
 * PURPOSE: Unit tests for AuthService using Jasmine.
 *          Tests basic session management logic and localStorage persistence.
 */
import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';
import { AuthResponse } from '../models/models';

describe('AuthService', () => {
  let service: AuthService;
  const KEY = 'iqsure_user';

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AuthService);
    // Clear localStorage before each test to ensure isolation
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should save user to localStorage', () => {
    const dummyUser: AuthResponse = {
      token: 'fake-jwt-token',
      userId: 1,
      name: 'Test user',
      role: 'ROLE_USER',
      email: 'test@example.com'
    };

    service.saveUser(dummyUser);
    const stored = JSON.parse(localStorage.getItem(KEY) || '{}');
    expect(stored.name).toBe('Test user');
    expect(stored.token).toBe('fake-jwt-token');
  });

  it('should return true for isLoggedIn when user exists', () => {
    localStorage.setItem(KEY, JSON.stringify({ userId: 1 }));
    expect(service.isLoggedIn()).toBeTrue();
  });

  it('should return false for isLoggedIn when no user exists', () => {
    expect(service.isLoggedIn()).toBeFalse();
  });

  it('should identify admin correctly', () => {
    localStorage.setItem(KEY, JSON.stringify({ role: 'ROLE_ADMIN' }));
    expect(service.isAdmin()).toBeTrue();

    localStorage.setItem(KEY, JSON.stringify({ role: 'ROLE_USER' }));
    expect(service.isAdmin()).toBeFalse();
  });

  it('should clear localStorage on logout', () => {
    localStorage.setItem(KEY, JSON.stringify({ userId: 1 }));
    service.logout();
    expect(localStorage.getItem(KEY)).toBeNull();
  });
});
