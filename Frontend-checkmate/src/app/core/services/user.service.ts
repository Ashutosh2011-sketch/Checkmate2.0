import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class UserService {

  private api = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  //  GET ALL USERS
  getAll() {
    return this.http.get<any[]>(this.api);
  }

  // CREATE USER
  create(user: any) {
    return this.http.post(this.api, user);
  }

  //  UPDATE USER
  update(id: number, user: any) {
    return this.http.put(`${this.api}/${id}`, user);
  }

  //  DELETE USER
  delete(id: number) {
    return this.http.delete(`${this.api}/${id}`);
  }

  //  NEW: GET TASKS FROM CHECKLIST
  getUserTasks(name: string) {
    return this.http.get<string[]>(`${this.api}/${name}/tasks`);
  }
}