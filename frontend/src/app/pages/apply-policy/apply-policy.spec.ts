/*
 * FILE: apply-policy.spec.ts
 * PURPOSE: Unit tests for ApplyPolicyComponent.
 */
import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ApplyPolicyComponent } from './apply-policy';
import { ApiService } from '../../services/api.service';
import { AuthService } from '../../services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { of } from 'rxjs';
import { FormsModule } from '@angular/forms';
import { provideRouter } from '@angular/router';

describe('ApplyPolicyComponent', () => {
  let component: ApplyPolicyComponent;
  let fixture: ComponentFixture<ApplyPolicyComponent>;
  let apiMock: any;
  let authMock: any;

  beforeEach(async () => {
    apiMock = {
      getActivePolicies: jasmine.createSpy('getActivePolicies').and.returnValue(of([])),
      getAvailableRewardsForUser: jasmine.createSpy('getAvailableRewardsForUser').and.returnValue(of([])),
      getAllDiscountRules: jasmine.createSpy('getAllDiscountRules').and.returnValue(of([])),
      getProfile: jasmine.createSpy('getProfile').and.returnValue(of({})),
      calculatePremium: jasmine.createSpy('calculatePremium').and.returnValue(of({})),
      purchasePolicy: jasmine.createSpy('purchasePolicy').and.returnValue(of({}))
    };

    authMock = {
      getUserId: jasmine.createSpy('getUserId').and.returnValue(1),
      getUser: jasmine.createSpy('getUser').and.returnValue({ name: 'User' })
    };

    await TestBed.configureTestingModule({
      imports: [ApplyPolicyComponent, FormsModule],
      providers: [
        { provide: ApiService, useValue: apiMock },
        { provide: AuthService, useValue: authMock },
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ApplyPolicyComponent);
    component = fixture.componentInstance;
  });

  it('should load initial data on ngOnInit', () => {
    fixture.detectChanges();
    expect(apiMock.getActivePolicies).toHaveBeenCalled();
    expect(apiMock.getAvailableRewardsForUser).toHaveBeenCalled();
  });

  it('should update step and add self when plan is selected', () => {
    const dummyPlan = { policyId: 1, title: 'Gold' } as any;
    component.selectPlan(dummyPlan);

    expect(component.step).toBe('FORM');
    expect(component.selectedPlan).toEqual(dummyPlan);
    expect(component.formData.members[0].fullName).toBe('User');
  });

  it('should add additional members correctly', () => {
    component.addMember();
    expect(component.formData.members.length).toBe(1);
    component.addMember();
    expect(component.formData.members.length).toBe(2);
  });

  it('should call api.purchasePolicy on submit', fakeAsync(() => {
    component.selectedPlan = { policyId: 10 } as any;
    component.formData.nomineeName = 'Jane';
    
    component.submitRequest();
    tick();

    expect(apiMock.purchasePolicy).toHaveBeenCalled();
    expect(component.successMessage).toContain('successfully');
  }));
});
