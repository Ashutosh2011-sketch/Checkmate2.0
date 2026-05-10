import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { TEMPLATE_LIBRARY, type ChecklistTemplateValue } from './template-library';
import { ChecklistService } from '../../core/services/checklist.service';
import { UserService } from '../../core/services/user.service';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';

// eslint-disable-next-line @typescript-eslint/no-explicit-any
const getGapi = (): any => (window as any)['gapi'];
// eslint-disable-next-line @typescript-eslint/no-explicit-any
const getGoogle = (): any => (window as any)['google'];

export interface TaskAttachmentPreview {
  fileName: string;
  fileType: string;
  fileSize: number;
  sourceType: 'LOCAL' | 'GOOGLE_DRIVE';
  file?: File;
  driveFileUrl?: string;
  driveFileId?: string;
}

@Component({
  selector: 'app-checklist-builder',
  templateUrl: './checklist-builder.component.html',
  styleUrls: ['./checklist-builder.component.css']
})
export class ChecklistBuilderComponent implements OnInit {

  checklistForm!: FormGroup;
  departments = ['HR', 'IT', 'Engineering', 'Finance'];
  availableUsers: string[] = [];
  availableTasksForDependency: string[] = [];
  selectedTemplate: string = 'blank';

  taskAttachments: Map<string, TaskAttachmentPreview[]> = new Map();
  showAttachMenu: Map<string, boolean> = new Map();

  private readonly GOOGLE_API_KEY = 'AIzaSyCHs4daSBJAmTYZ4YkvezX7AB7yQq3b7yI';
  private readonly GOOGLE_CLIENT_ID = 

'1062090854163-nr8e4rrearei8nsn1tah9r2af3cpu58s.apps.googleusercontent.com';
  private readonly GOOGLE_PICKER_SCOPE = 'https://www.googleapis.com/auth/drive.readonly';

  private googlePickerReady = false;
  private oauthToken: string | null = null;

  constructor(
    private fb: FormBuilder,
    private checklistService: ChecklistService,
    private userService: UserService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.checklistForm = this.fb.group({
      checklistName: ['', Validators.required],
      department: ['', Validators.required],
      visibility: ['Private', Validators.required],
      workflowType: ['Sequential', Validators.required],
      sections: this.fb.array([])
    });

    this.loadUsers();
    this.addSection();
    this.loadGooglePickerApi();
  }

  // ── Google Picker ─────────────────────────────────────────────────────────

  private loadGooglePickerApi(): void {
    if (!getGapi()) {
      const script = document.createElement('script');
      script.src = 'https://apis.google.com/js/api.js';
      script.onload = () => {
        getGapi()?.load('picker', () => {
          this.googlePickerReady = true;
        });
      };
      document.head.appendChild(script);

      const gsiScript = document.createElement('script');
      gsiScript.src = 'https://accounts.google.com/gsi/client';
      document.head.appendChild(gsiScript);
    } else {
      getGapi()?.load('picker', () => {
        this.googlePickerReady = true;
      });
    }
  }

  private getOAuthToken(callback: (token: string) => void): void {
    if (this.oauthToken) {
      callback(this.oauthToken);
      return;
    }

    const tokenClient = getGoogle().accounts.oauth2.initTokenClient({
      client_id: this.GOOGLE_CLIENT_ID,
      scope: this.GOOGLE_PICKER_SCOPE,
      callback: (response: any) => {
        if (response.access_token) {
          this.oauthToken = response.access_token;
          callback(response.access_token);
        }
      }
    });
    tokenClient.requestAccessToken();
  }

  // ── Attachment Methods ────────────────────────────────────────────────────

  getAttachmentKey(sIndex: number, tIndex: number): string {
    return `${sIndex}_${tIndex}`;
  }

  getAttachments(sIndex: number, tIndex: number): TaskAttachmentPreview[] {
    return this.taskAttachments.get(this.getAttachmentKey(sIndex, tIndex)) || [];
  }

  toggleAttachMenu(sIndex: number, tIndex: number): void {
    const key = this.getAttachmentKey(sIndex, tIndex);
    const current = this.showAttachMenu.get(key) || false;
    this.showAttachMenu.clear();
    this.showAttachMenu.set(key, !current);
  }

  isAttachMenuOpen(sIndex: number, tIndex: number): boolean {
    return this.showAttachMenu.get(this.getAttachmentKey(sIndex, tIndex)) || false;
  }

  closeAttachMenus(): void {
    this.showAttachMenu.clear();
  }

  onLocalFileSelected(event: Event, sIndex: number, tIndex: number): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;

    const file = input.files[0];
    const key = this.getAttachmentKey(sIndex, tIndex);

    if (!this.taskAttachments.has(key)) {
      this.taskAttachments.set(key, []);
    }

    this.taskAttachments.get(key)!.push({
      fileName: file.name,
      fileType: file.type,
      fileSize: file.size,
      sourceType: 'LOCAL',
      file: file
    });

    this.closeAttachMenus();
    input.value = '';
  }

  openGoogleDrivePicker(sIndex: number, tIndex: number): void {
    if (!this.googlePickerReady) {
      alert('Google Picker is loading, please try again.');
      return;
    }

    this.getOAuthToken((token) => {
      const picker = new (getGoogle().picker.PickerBuilder)()
        .addView(getGoogle().picker.ViewId.DOCS)
        .setOAuthToken(token)
        .setDeveloperKey(this.GOOGLE_API_KEY)
        .setCallback((data: any) => {
          if (data.action === getGoogle().picker.Action.PICKED) {
            const doc = data.docs[0];
            const key = this.getAttachmentKey(sIndex, tIndex);

            if (!this.taskAttachments.has(key)) {
              this.taskAttachments.set(key, []);
            }

            this.taskAttachments.get(key)!.push({
              fileName: doc.name,
              fileType: doc.mimeType,
              fileSize: doc.sizeBytes || 0,
              sourceType: 'GOOGLE_DRIVE',
              driveFileUrl: doc.url,
              driveFileId: doc.id
            });
          }
        })
        .build();

      picker.setVisible(true);
      this.closeAttachMenus();
    });
  }

  removeAttachment(sIndex: number, tIndex: number, aIndex: number): void {
    const key = this.getAttachmentKey(sIndex, tIndex);
    const attachments = this.taskAttachments.get(key) || [];
    attachments.splice(aIndex, 1);
    this.taskAttachments.set(key, attachments);
  }

  formatFileSize(bytes: number): string {
    if (bytes === 0) return 'Drive file';
    if (bytes < 1024) return bytes + ' B';
    if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB';
    return (bytes / (1024 * 1024)).toFixed(1) + ' MB';
  }

  private async uploadAttachmentsForTask(taskId: number, attachments: TaskAttachmentPreview[]): Promise<void> {
    for (const attachment of attachments) {
      if (attachment.sourceType === 'LOCAL' && attachment.file) {
        const formData = new FormData();
        formData.append('file', attachment.file);
        formData.append('uploadedBy', 'Admin');
        await this.http.post(
          `${environment.apiUrl}/collaboration/tasks/${taskId}/attachments`,
          formData
        ).toPromise();
      } else if (attachment.sourceType === 'GOOGLE_DRIVE' && attachment.driveFileUrl) {
        await this.http.post(
          `${environment.apiUrl}/collaboration/tasks/${taskId}/attachments/drive`,
          {
            fileName: attachment.fileName,
            driveFileUrl: attachment.driveFileUrl,
            uploadedBy: 'Admin'
          }
        ).toPromise();
      }
    }
  }

  // ── Form Methods ──────────────────────────────────────────────────────────

  loadUsers() {
    this.userService.getAll().subscribe((res: any[]) => {
      this.availableUsers = res.map(user => `${user.name} (${user.department})`);
    });
  }

  get sections(): FormArray {
    return this.checklistForm.get('sections') as FormArray;
  }

  tasks(sectionIndex: number): FormArray {
    return this.sections.at(sectionIndex).get('tasks') as FormArray;
  }

  private createTaskGroup(task?: Partial<ChecklistTemplateValue['sections'][number]['tasks'][number]>): FormGroup {
    return this.fb.group({
      title: [task?.title ?? '', Validators.required],
      description: [task?.description ?? ''],
      assignees: [task?.assignees ?? []],
      priority: [task?.priority ?? 'Medium'],
      dueDateDays: [task?.dueDateDays ?? 1, [Validators.required, Validators.min(1)]],
      dependsOn: [task?.dependsOn ?? 'None'],
      conditionDependentOn: [task?.conditionDependentOn ?? 'None'],
      conditionExpectedOutcome: [task?.conditionExpectedOutcome ?? 'Pass'],
      remindBefore: [task?.remindBefore ?? 1, [Validators.min(1), Validators.max(5)]],
      escalateTo: [task?.escalateTo ?? 'Manager'],
      showAdvanced: [task?.showAdvanced ?? false]
    });
  }

  private createSectionGroup(section?: Partial<ChecklistTemplateValue['sections'][number]>): FormGroup {
    const tasksArray = this.fb.array<FormGroup>([]);
    (section?.tasks ?? []).forEach(t => tasksArray.push(this.createTaskGroup(t)));
    return this.fb.group({
      sectionName: [section?.sectionName ?? 'New Section', Validators.required],
      tasks: tasksArray
    });
  }

  addSection(): void {
    const sectionGroup = this.createSectionGroup({ sectionName: 'New Section', tasks: [] });
    this.sections.push(sectionGroup);
    this.addTask(this.sections.length - 1);
  }

  removeSection(index: number): void {
    this.sections.removeAt(index);
    this.updateDependenciesList();
  }

  addTask(sectionIndex: number, task?: Partial<ChecklistTemplateValue['sections'][number]['tasks'][number]>): void {
    const taskGroup = this.createTaskGroup(task);
    this.tasks(sectionIndex).push(taskGroup);
    this.updateDependenciesList();
  }

  removeTask(sectionIndex: number, taskIndex: number): void {
    this.tasks(sectionIndex).removeAt(taskIndex);
    this.taskAttachments.delete(this.getAttachmentKey(sectionIndex, taskIndex));
    this.updateDependenciesList();
  }

  toggleAdvanced(sectionIndex: number, taskIndex: number): void {
    const task = this.tasks(sectionIndex).at(taskIndex);
    task.patchValue({ showAdvanced: !task.value.showAdvanced });
  }

  drop(event: CdkDragDrop<any[]>, sectionIndex: number): void {
    const tasksArray = this.tasks(sectionIndex);
    moveItemInArray(tasksArray.controls, event.previousIndex, event.currentIndex);
    tasksArray.updateValueAndValidity();
  }

  updateDependenciesList(): void {
    this.availableTasksForDependency = [];
    this.sections.controls.forEach(sec => {
      const tasks = sec.get('tasks') as FormArray;
      tasks.controls.forEach(task => {
        if (task.value.title) {
          this.availableTasksForDependency.push(task.value.title);
        }
      });
    });
  }

  loadTemplate(templateName: string): void {
    this.selectedTemplate = templateName;
    while (this.sections.length) this.sections.removeAt(0);
    this.taskAttachments.clear();

    if (!templateName || templateName === 'blank') {
      this.checklistForm.patchValue({ checklistName: '', department: '', visibility: 'Private', workflowType: 'Sequential' });
      this.addSection();
      this.updateDependenciesList();
      return;
    }

    const template = TEMPLATE_LIBRARY[templateName];
    if (!template) { this.addSection(); this.updateDependenciesList(); return; }

    this.checklistForm.patchValue({
      checklistName: template.checklistName,
      department: template.department,
      visibility: template.visibility,
      workflowType: template.workflowType
    });

    template.sections.forEach(section => {
      const sectionGroup = this.createSectionGroup({ sectionName: section.sectionName, tasks: [] });
      this.sections.push(sectionGroup);
      section.tasks.forEach(task => this.addTask(this.sections.length - 1, task));
    });

    this.updateDependenciesList();
  }

  async onSubmit(): Promise<void> {
    if (this.checklistForm.invalid) return;

    this.checklistService.createChecklist(this.checklistForm.value).subscribe({
      next: async (response: any) => {
        console.log('Checklist saved:', response);

        if (response.sections) {
          for (let sIndex = 0; sIndex < response.sections.length; sIndex++) {
            const section = response.sections[sIndex];
            for (let tIndex = 0; tIndex < section.tasks.length; tIndex++) {
              const taskId = section.tasks[tIndex].id;
              const attachments = this.getAttachments(sIndex, tIndex);
              if (attachments.length > 0 && taskId) {
                await this.uploadAttachmentsForTask(taskId, attachments);
              }
            }
          }
        }

        alert('Checklist saved!');
        this.checklistForm.reset();
        this.taskAttachments.clear();
      },
      error: (error: any) => {
        console.error('Error:', error);
      }
    });
  }
}