/*
 * FILE: dashboard.spec.ts
 * PURPOSE: Unit tests for ClaimsOfficerDashboardComponent.
 */
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ClaimsOfficerDashboardComponent } from './dashboard';
import { ApiService } from '../../../services/api.service';
import { AuthService } from '../../../services/auth.service';
import { of } from 'rxjs';
import { provideRouter } from '@angular/router';

describe('ClaimsOfficerDashboardComponent', () => {
  let component: ClaimsOfficerDashboardComponent;
  let fixture: ComponentFixture<ClaimsOfficerDashboardComponent>;
  let apiMock: any;
  let authMock: any;

  beforeEach(async () => {
    apiMock = {
      getClaimsOfficerStats: jasmine.createSpy('getClaimsOfficerStats').and.returnValue(of({ approved: 5, approvalRate: '50%' }))
    };
    authMock = {
      getUser: jasmine.createSpy('getUser').and.returnValue({ name: 'Officer Smith', employeeId: 'E99' }),
      getUserId: jasmine.createSpy('getUserId').and.returnValue(2)
    };

    await TestBed.configureTestingModule({
      imports: [ClaimsOfficerDashboardComponent],
      providers: [
        { provide: ApiService, useValue: apiMock },
        { provide: AuthService, useValue: authMock },
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ClaimsOfficerDashboardComponent);
    component = fixture.componentInstance;
  });

  it('should initialize with user info and claims stats', () => {
    fixture.detectChanges();

    expect(component.userName).toBe('Officer Smith');
    expect(apiMock.getClaimsOfficerStats).toHaveBeenCalledWith(2);
    expect(component.stats.approved).toBe(5);
    expect(component.stats.approvalRate).toBe('50%');
  });
});
