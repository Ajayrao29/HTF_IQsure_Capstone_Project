/*
 * FILE: api.service.spec.ts
 * PURPOSE: Unit tests for ApiService using Jasmine and HttpClientTestingModule.
 *          Tests that HTTP requests are sent to the correct endpoints.
 */
import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ApiService } from './api.service';

describe('ApiService', () => {
  let service: ApiService;
  let httpMock: HttpTestingController;
  const API = 'http://localhost:8080';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ApiService]
    });
    service = TestBed.inject(ApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    // Ensure that there are no outstanding requests
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should call login API with POST', () => {
    const dummyData = { email: 'user@test.com', password: '123' };
    const dummyResponse = { token: 'jwt-123', userId: 1 };

    service.login(dummyData).subscribe(res => {
      expect(res).toEqual(dummyResponse);
    });

    const req = httpMock.expectOne(`${API}/api/auth/login`);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(dummyData);
    req.flush(dummyResponse); // Simulate server response
  });

  it('should fetch user profile with GET', () => {
    const userId = 1;
    const dummyProfile = { userId: 1, name: 'John Doe' };

    service.getProfile(userId).subscribe(profile => {
      expect(profile.name).toBe('John Doe');
    });

    const req = httpMock.expectOne(`${API}/api/v1/users/${userId}`);
    expect(req.request.method).toBe('GET');
    req.flush(dummyProfile);
  });

  it('should fetch all claims for admin', () => {
    const dummyClaims = [{ id: 1, amount: 500 }, { id: 2, amount: 1000 }];

    service.getAllClaimsAdmin().subscribe(claims => {
      expect(claims.length).toBe(2);
      expect(claims[0].id).toBe(1);
    });

    const req = httpMock.expectOne(`${API}/api/v1/claims/all`);
    expect(req.request.method).toBe('GET');
    req.flush(dummyClaims);
  });

  it('should call processClaim with correct parameters', () => {
    const claimId = 101;
    const status = 'APPROVED';
    const remarks = 'Looks good';
    const approvedAmount = 450;

    service.processClaim(claimId, status, remarks, approvedAmount).subscribe();

    const req = httpMock.expectOne(request => 
      request.url.includes(`/api/v1/claims/${claimId}/process`) &&
      request.params.get('status') === status &&
      request.params.get('remarks') === remarks &&
      request.params.get('approvedAmount') === approvedAmount.toString()
    );
    
    expect(req.request.method).toBe('PUT');
    req.flush({});
  });
});
