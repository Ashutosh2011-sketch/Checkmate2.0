import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Role, Permission } from '../models/roles-permissions.model';
import { environment } from '../../../environments/environment';

export interface UserPermissionInfo {
  userId: number;
  userName: string;
  email: string;
  designation: string;
  permissions: Permission[];
}

@Injectable({ providedIn: 'root' })
export class RolesPermissionsService {

  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // ===== ROLE-LEVEL =====
  getAllRoles(): Observable<Role[]> {
    return this.http.get<Role[]>(`${this.apiUrl}/roles`);
  }

  getRoleById(id: number): Observable<Role> {
    return this.http.get<Role>(`${this.apiUrl}/roles/${id}`);
  }

  createRole(role: Role): Observable<Role> {
    return this.http.post<Role>(`${this.apiUrl}/roles`, role);
  }

  updateRolePermissions(roleId: number, permissions: Permission[]): Observable<Role> {
    return this.http.put<Role>(`${this.apiUrl}/roles/${roleId}/permissions`, permissions);
  }

  deleteRole(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/roles/${id}`);
  }

  getAllPermissions(): Observable<Permission[]> {
    return this.http.get<Permission[]>(`${this.apiUrl}/permissions`);
  }

  // ===== USER-LEVEL =====
  getUsersByRole(roleName: string): Observable<UserPermissionInfo[]> {
    return this.http.get<UserPermissionInfo[]>(`${this.apiUrl}/role-users/${roleName}`);
  }

  getUserPermissions(userId: number): Observable<UserPermissionInfo> {
    return this.http.get<UserPermissionInfo>(`${this.apiUrl}/user-permissions/${userId}`);
  }

  updateUserPermissions(userId: number, permissions: Permission[]): Observable<UserPermissionInfo> {
    return this.http.put<UserPermissionInfo>(`${this.apiUrl}/user-permissions/${userId}`, permissions);
  }
}
