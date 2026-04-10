import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders} from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ProfileUpdateRequest {
  fullName: string;
  jobTitle: string;
  department: string;
  currentPassword?: string;
  newPassword?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ProfileService {

  private apiUrl = 'http://localhost:8080/api/users/update-profile';

  constructor(private http: HttpClient) { }

 
    private getHeaders() {
   
    const token = localStorage.getItem('token'); 
    return new HttpHeaders({
      'Authorization': `Bearer ${token}`
    });
  }
  
  updateProfile(profileData: ProfileUpdateRequest): Observable<any> {
    return this.http.put(this.apiUrl, profileData, { headers: this.getHeaders(),
      responseType: 'text' });
  }

  getCurrentUser(): Observable<any> {
  const token = localStorage.getItem('token'); 
  return this.http.get('http://localhost:8080/api/users/me', {
    headers: new HttpHeaders().set('Authorization', `Bearer ${token}`) 
  });
}
}