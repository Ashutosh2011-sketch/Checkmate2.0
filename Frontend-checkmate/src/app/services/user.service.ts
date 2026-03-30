import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class UserService {

  private api = 'http://localhost:8080/api/users';

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
}