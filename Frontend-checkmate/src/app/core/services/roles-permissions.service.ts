import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Role, Permission } from '../models/roles-permissions.model';

@Injectable({
  providedIn: 'root'
})
export class RolesPermissionsService {

  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // GET ALL ROLES
  getAllRoles(): Observable<Role[]> {
    return this.http.get<Role[]>(`${this.apiUrl}/roles`);
  }

  // GET SINGLE ROLE
  getRoleById(id: number): Observable<Role> {
    return this.http.get<Role>(`${this.apiUrl}/roles/${id}`);
  }

  // CREATE ROLE
  createRole(role: Role): Observable<Role> {
    return this.http.post<Role>(`${this.apiUrl}/roles`, role);
  }

  // UPDATE ROLE PERMISSIONS
  updateRolePermissions(roleId: number, permissions: Permission[]): Observable<Role> {
    return this.http.put<Role>(`${this.apiUrl}/roles/${roleId}/permissions`, permissions);
  }

  // DELETE ROLE
  deleteRole(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/roles/${id}`);
  }

  // GET ALL PERMISSIONS
  getAllPermissions(): Observable<Permission[]> {
    return this.http.get<Permission[]>(`${this.apiUrl}/permissions`);
  }
}
