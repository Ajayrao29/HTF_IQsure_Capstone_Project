/*
 * FILE: dashboard.spec.ts
 * PURPOSE: Unit tests for DashboardComponent (Shared Admin/User dashboard).
 */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { DashboardComponent } from './dashboard';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { of } from 'rxjs';
import { provideRouter } from '@angular/router';

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let apiMock: any;
  let authMock: any;

  beforeEach(async () => {
    apiMock = {
      getAllUsers: jasmine.createSpy('getAllUsers').and.returnValue(of([])),
      getAllQuizzes: jasmine.createSpy('getAllQuizzes').and.returnValue(of([])),
      getAllPolicies: jasmine.createSpy('getAllPolicies').and.returnValue(of([])),
      getAllRewards: jasmine.createSpy('getAllRewards').and.returnValue(of([])),
      getAllDiscountRules: jasmine.createSpy('getAllDiscountRules').and.returnValue(of([])),
      getProfile: jasmine.createSpy('getProfile').and.returnValue(of({})),
      getBadgesByUser: jasmine.createSpy('getBadgesByUser').and.returnValue(of([])),
      getAttemptsByUser: jasmine.createSpy('getAttemptsByUser').and.returnValue(of([])),
      getUserPolicies: jasmine.createSpy('getUserPolicies').and.returnValue(of([])),
      getClaimsByUser: jasmine.createSpy('getClaimsByUser').and.returnValue(of([]))
    };

    authMock = {
      isAdmin: jasmine.createSpy('isAdmin'),
      getUserId: jasmine.createSpy('getUserId').and.returnValue(1)
    };

    await TestBed.configureTestingModule({
      imports: [DashboardComponent],
      providers: [
        { provide: ApiService, useValue: apiMock },
        { provide: AuthService, useValue: authMock },
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
  });

  it('should load Admin Dashboard if user is admin', fakeAsync(() => {
    authMock.isAdmin.and.returnValue(true);
    const dummyUsers = [{ role: 'ROLE_ADMIN' }, { role: 'ROLE_USER' }];
    apiMock.getAllUsers.and.returnValue(of(dummyUsers));

    fixture.detectChanges(); // ngOnInit
    tick();

    expect(apiMock.getAllUsers).toHaveBeenCalled();
    expect(component.stats.totalUsers).toBe(2);
    expect(component.stats.totalAdmins).toBe(1);
    expect(component.loading).toBeFalse();
  }));

  it('should load User Dashboard if user is not admin', fakeAsync(() => {
    authMock.isAdmin.and.returnValue(false);
    apiMock.getProfile.and.returnValue(of({ name: 'John', userPoints: 500 }));

    fixture.detectChanges();
    tick();

    expect(apiMock.getProfile).toHaveBeenCalledWith(1);
    expect(component.user.name).toBe('John');
    expect(component.loading).toBeFalse();
  }));

  it('should calculate total savings correctly', fakeAsync(() => {
    authMock.isAdmin.and.returnValue(false);
    const dummyPolicies = [{ savedAmount: 100 }, { savedAmount: 250 }];
    apiMock.getUserPolicies.and.returnValue(of(dummyPolicies));

    fixture.detectChanges();
    tick();

    expect(component.totalSavings).toBe(350);
  }));
});
