// import { NgModule } from '@angular/core';
// import { BrowserModule } from '@angular/platform-browser';
// import { AppRoutingModule } from './app-routing.module';
// import { FormsModule } from '@angular/forms';
// import { CommonModule } from '@angular/common';

// import { AppComponent } from './app.component';
// import { SidebarComponent } from './layout/sidebar/sidebar.component';
// import { HeaderComponent } from './layout/header/header.component';
// import { UserDashboardComponent } from './pages/user-dashboard/user-dashboard.component';
// import { ChecklistDetailComponent } from './pages/checklist-detail/checklist-detail.component';
// import { LandingPageComponent } from './pages/landing-page/landing-page.component';
// import { LoginComponent } from './pages/login/login.component';
// import { AdminDashboardComponent } from './pages/admin-dashboard/admin-dashboard.component';
// import { RolesPermissionsComponent } from '../app/pages/roles-permissions/roles-permissions.component'
// import { AddRoleDrawerComponent } from './pages/roles-permissions/add-role-drawer/add-role-drawer.component';

// import { ChecklistBuilderComponent } from './pages/checklist-builder/checklist-builder.component';
// import { ChecklistTrackerComponent } from './pages/checklist-tracker/checklist-tracker.component';
// import { UserManagementComponent } from './pages/user-management/user-management.component';
// import { SecurityComplianceComponent } from './pages/security-compliance/security-compliance.component';

// // New components
// import { ReportsComponent } from './pages/reports/reports.component';
// import { ProfileComponent } from './pages/profile/profile.component';
// import { NotFoundComponent } from './pages/not-found/not-found.component';

// import { AdminTasksComponent } from './pages/admin/admin-tasks/admin-tasks.component';
// import { AdminRolesComponent } from './pages/admin/admin-roles/admin-roles.component';
// import { AdminDepartmentsComponent } from './pages/admin/admin-departments/admin-departments.component';
// import { AdminTemplatesComponent } from './pages/admin/admin-templates/admin-templates.component';
// import { AdminWorkflowComponent } from './pages/admin/admin-workflow/admin-workflow.component';
// import { AdminReportsComponent } from './pages/admin/admin-reports/admin-reports.component';
// import { AdminNotificationsComponent } from './pages/notifications/notifications.component';
// import { AdminProfileComponent } from './pages/admin/admin-profile/admin-profile.component';
// import { AllChecklistComponent } from './pages/all-checklists/all-checklists.component';

// @NgModule({
//   declarations: [
//     AppComponent,
//     SidebarComponent,
//     HeaderComponent,
//     UserDashboardComponent,
//     ChecklistDetailComponent,
//     LandingPageComponent,
//     LoginComponent,
//     AdminDashboardComponent,
//     ChecklistBuilderComponent,
//     ChecklistTrackerComponent,
//     UserManagementComponent,
//     SecurityComplianceComponent,
//     ReportsComponent,
//     ProfileComponent,
//     NotFoundComponent,
//     AllChecklistComponent,
//     AdminTasksComponent,
//     AdminRolesComponent,
//     AdminDepartmentsComponent,
//     AdminTemplatesComponent,
//     AdminWorkflowComponent,
//     AdminReportsComponent,
//     AdminNotificationsComponent,
//     AdminProfileComponent,
//     RolesPermissionsComponent,
//     AddRoleDrawerComponent,
//     AdminProfileComponent,
//   ],
//   imports: [
//     BrowserModule,
//     AppRoutingModule,
//     FormsModule,
//     CommonModule
//   ],
//   providers: [],
//   bootstrap: [AppComponent]
// })
// export class AppModule { }
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { AppRoutingModule } from './app-routing.module';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { ReactiveFormsModule } from '@angular/forms';

import { AppComponent } from './app.component';
import { SidebarComponent } from './layout/sidebar/sidebar.component';
import { HeaderComponent } from './layout/header/header.component';
import { UserDashboardComponent } from './pages/user-dashboard/user-dashboard.component';
import { ChecklistDetailComponent } from './pages/checklist-detail/checklist-detail.component';
import { LandingPageComponent } from './pages/landing-page/landing-page.component';
import { LoginComponent } from './pages/login/login.component';
import { AdminDashboardComponent } from './pages/admin-dashboard/admin-dashboard.component';

// YAHAN RASTA THEEK KAR DIYA HAI 👇
import { RolesPermissionsComponent } from './pages/roles-permissions/roles-permissions.component';
import { AddRoleDrawerComponent } from './pages/roles-permissions/add-role-drawer/add-role-drawer.component';

import { ChecklistBuilderComponent } from './pages/checklist-builder/checklist-builder.component';
import { ChecklistTrackerComponent } from './pages/checklist-tracker/checklist-tracker.component';

import { UserManagementComponent } from './pages/user-management/user-management.component';
import { SecurityComplianceComponent } from './pages/security-compliance/security-compliance.component';

import { ReportsComponent } from './pages/reports/reports.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { NotFoundComponent } from './pages/not-found/not-found.component';

import { AdminTasksComponent } from './pages/admin/admin-tasks/admin-tasks.component';
import { AdminRolesComponent } from './pages/admin/admin-roles/admin-roles.component';
import { AdminDepartmentsComponent } from './pages/admin/admin-departments/admin-departments.component';
import { AdminTemplatesComponent } from './pages/admin/admin-templates/admin-templates.component';
import { AdminWorkflowComponent } from './pages/admin/admin-workflow/admin-workflow.component';
import { AdminReportsComponent } from './pages/admin/admin-reports/admin-reports.component';
import { AdminNotificationsComponent } from './pages/notifications/notifications.component';
import { AdminProfileComponent } from './pages/admin/admin-profile/admin-profile.component';
import { AllChecklistComponent } from './pages/all-checklists/all-checklists.component';

@NgModule({
  declarations: [
    AppComponent,
    SidebarComponent,
    HeaderComponent,
    UserDashboardComponent,
    ChecklistDetailComponent,
    LandingPageComponent,
    LoginComponent,
    AdminDashboardComponent,
    ChecklistBuilderComponent,
    ChecklistTrackerComponent,
    UserManagementComponent,
    SecurityComplianceComponent,
    ReportsComponent,
    ProfileComponent,
    NotFoundComponent,
    AllChecklistComponent,
    AdminTasksComponent,
    AdminRolesComponent,
    AdminDepartmentsComponent,
    AdminTemplatesComponent,
    AdminWorkflowComponent,
    AdminReportsComponent,
    AdminNotificationsComponent,
    AdminProfileComponent,
    RolesPermissionsComponent,
    AddRoleDrawerComponent
    // SAARI DUPLICATE ENTRIES HATA DI GAYI HAIN 🧹
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    CommonModule,
    DragDropModule,
    ReactiveFormsModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
