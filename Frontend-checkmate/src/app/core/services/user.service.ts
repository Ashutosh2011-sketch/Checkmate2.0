import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class UserService {

  private api = `${environment.apiUrl}/users`;

  constructor(private http: HttpClient) {}

  getAll() {
    return this.http.get<any[]>(this.api);
  }

  create(user: any) {
    return this.http.post(this.api, user);
  }

  update(id: number, user: any) {
    return this.http.put(`${this.api}/${id}`, user);
  }

  delete(id: number) {
    return this.http.delete(`${this.api}/${id}`);
  }

  getUserTasks(name: string) {
    return this.http.get<string[]>(`${this.api}/${name}/tasks`);
  }
}