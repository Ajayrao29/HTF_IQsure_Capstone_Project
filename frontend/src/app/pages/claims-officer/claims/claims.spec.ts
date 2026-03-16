/*
 * FILE: claims.spec.ts
 * PURPOSE: Component testing for ClaimsOfficerClaimsComponent.
 *          Demonstrates service mocking, change detection, and method testing.
 */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ClaimsOfficerClaimsComponent } from './claims';
import { ApiService } from '../../../services/api.service';
import { AuthService } from '../../../services/auth.service';
import { of, throwError } from 'rxjs';
import { provideRouter } from '@angular/router';
import { DecimalPipe } from '@angular/common';

describe('ClaimsOfficerClaimsComponent', () => {
  let component: ClaimsOfficerClaimsComponent;
  let fixture: ComponentFixture<ClaimsOfficerClaimsComponent>;
  let apiMock: any;
  let authMock: any;

  beforeEach(async () => {
    // Mock ApiService
    apiMock = {
      getAllClaimsAdmin: jasmine.createSpy('getAllClaimsAdmin').and.returnValue(of([])),
      processClaim: jasmine.createSpy('processClaim').and.returnValue(of({})),
      settleClaim: jasmine.createSpy('settleClaim').and.returnValue(of({}))
    };

    // Mock AuthService
    authMock = {
      getUserId: jasmine.createSpy('getUserId').and.returnValue(1)
    };

    await TestBed.configureTestingModule({
      imports: [ClaimsOfficerClaimsComponent],
      providers: [
        { provide: ApiService, useValue: apiMock },
        { provide: AuthService, useValue: authMock },
        provideRouter([]),
        DecimalPipe
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ClaimsOfficerClaimsComponent);
    component = fixture.componentInstance;
  });

  it('should create context', () => {
    expect(component).toBeTruthy();
  });

  it('should load claims on init', () => {
    const dummyClaims = [{ id: 1, claimNumber: 'CLM-1', status: 'SUBMITTED' }];
    apiMock.getAllClaimsAdmin.and.returnValue(of(dummyClaims));

    fixture.detectChanges(); // triggers ngOnInit

    expect(apiMock.getAllClaimsAdmin).toHaveBeenCalled();
    expect(component.claims.length).toBe(1);
    expect(component.filteredClaims.length).toBe(1);
  });

  it('should filter claims by status', () => {
    component.claims = [
      { id: 1, status: 'SUBMITTED' },
      { id: 2, status: 'APPROVED' }
    ] as any;

    component.applyFilter('APPROVED');
    expect(component.filteredClaims.length).toBe(1);
    expect(component.filteredClaims[0].id).toBe(2);

    component.applyFilter('ALL');
    expect(component.filteredClaims.length).toBe(2);
  });

  it('should open process modal and set defaults', () => {
    const dummyClaim = { id: 1, status: 'SUBMITTED', reviewerRemarks: 'Fix this', amount: 500 };
    component.openProcessModal(dummyClaim as any);

    expect(component.selectedClaim).toEqual(dummyClaim as any);
    expect(component.processingStatus).toBe('UNDER_REVIEW'); // Logic: if SUBMITTED -> UNDER_REVIEW
    expect(component.remarks).toBe('Fix this');
    expect(component.approvedAmount).toBe(500);
  });

  it('should call api.processClaim when submitProcess is called', fakeAsync(() => {
    component.selectedClaim = { id: 1 } as any;
    component.processingStatus = 'APPROVED';
    component.remarks = 'Verified';
    component.approvedAmount = 400;

    component.submitProcess();
    tick(); // process async observable

    expect(apiMock.processClaim).toHaveBeenCalledWith(1, 'APPROVED', 'Verified', 400);
    expect(component.notification?.message).toContain('success');
  }));

  it('should handle error when submitProcess fails', fakeAsync(() => {
    component.selectedClaim = { id: 1 } as any;
    apiMock.processClaim.and.returnValue(throwError(() => new Error('Fail')));

    component.submitProcess();
    tick();

    expect(component.notification?.type).toBe('error');
    expect(component.isProcessing).toBeFalse();
  }));
});
