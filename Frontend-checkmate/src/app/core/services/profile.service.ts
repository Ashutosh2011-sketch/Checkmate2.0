import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ProfileUpdateRequest {
  fullName: string;
  jobTitle: string;
  department: string;
  currentPassword?: string;
  newPassword?: string;
}

@Injectable({ providedIn: 'root' })
export class ProfileService {

  private api = environment.apiUrl;

  constructor(private http: HttpClient) {}

  updateProfile(profileData: ProfileUpdateRequest): Observable<any> {
    return this.http.put(`${this.api}/users/update-profile`, profileData, { responseType: 'text' });
  }

  getCurrentUser(): Observable<any> {
    return this.http.get(`${this.api}/users/me`);
  }
}