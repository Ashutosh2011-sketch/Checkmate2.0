import{ Component, OnInit } from '@angular/core';
import { TemplateService, TemplateDto, TemplateSectionDto, TemplateVersionDto } from '../../../core/services/template.service';

@Component({
  selector: 'app-admin-templates',
  templateUrl: './admin-templates.component.html',
  styleUrls: ['./admin-templates.component.css']
})
export class AdminTemplatesComponent implements OnInit {

  // ── Data ──────────────────────────────────────────────────────────────────
  allTemplates: TemplateDto[] = [];
  filteredTemplates: TemplateDto[] = [];
  versions: TemplateVersionDto[] = [];

  departments = ['HR', 'Engineering', 'Finance', 'Operations', 'Legal', 'IT'];

  // ── Filters ───────────────────────────────────────────────────────────────
  searchQuery = '';
  filterDept = '';
  filterVisibility = '';

  // ── UI State ──────────────────────────────────────────────────────────────
  loading = false;
  loadingVersions = false;
  saving = false;
  showFormModal = false;
  showVersionModal = false;
  showDeleteModal = false;
  editMode = false;
  showToast = false;
  toastMessage = '';

  // ── Selected ──────────────────────────────────────────────────────────────
  selectedTemplate: TemplateDto | null = null;
  templateToDelete: TemplateDto | null = null;

  // ── Form ──────────────────────────────────────────────────────────────────
  changeNote = '';
  form: TemplateDto = this.emptyForm();

  constructor(private templateService: TemplateService) {}

  ngOnInit(): void {
    this.loadTemplates();
  }

  // ── Load ──────────────────────────────────────────────────────────────────
  loadTemplates(): void {
    this.loading = true;
    this.templateService.getAll().subscribe({
      next: (data) => {
        this.allTemplates = data;
        this.applyFilters();
        this.loading = false;
      },
      error: (err) => {
        console.error('Load error:', err);
        this.loading = false;
        this.toast('Failed to load templates');
      }
    });
  }

  // ── Filters ───────────────────────────────────────────────────────────────
  applyFilters(): void {
    this.filteredTemplates = this.allTemplates.filter(t => {
      const matchSearch = !this.searchQuery ||
        t.templateName.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        (t.description || '').toLowerCase().includes(this.searchQuery.toLowerCase());
      const matchDept = !this.filterDept || t.department === this.filterDept;
      const matchVis = !this.filterVisibility || t.visibility === this.filterVisibility;
      return matchSearch && matchDept && matchVis;
    });
  }

  // ── Create ────────────────────────────────────────────────────────────────
  openCreateModal(): void {
    this.editMode = false;
    this.form = this.emptyForm();
    this.changeNote = '';
    this.showFormModal = true;
  }

  // ── Edit ──────────────────────────────────────────────────────────────────
  openEditModal(t: TemplateDto): void {
    this.editMode = true;
    this.selectedTemplate = t;
    this.changeNote = '';
    // Load full template with sections
    this.templateService.getById(t.id!).subscribe({
      next: (full) => {
        this.form = {
          ...full,
          sections: full.sections || []
        };
        this.showFormModal = true;
      },
      error: () => {
        this.form = { ...t, sections: t.sections || [] };
        this.showFormModal = true;
      }
    });
  }

  closeFormModal(): void {
    this.showFormModal = false;
    this.form = this.emptyForm();
  }

  // ── Save ──────────────────────────────────────────────────────────────────
  saveTemplate(): void {
    if (!this.form.templateName || !this.form.department || !this.form.visibility) {
      this.toast('Please fill all required fields');
      return;
    }
    this.saving = true;
    const call = this.editMode
      ? this.templateService.update(this.selectedTemplate!.id!, this.form, this.changeNote)
      : this.templateService.create(this.form);

    call.subscribe({
      next: () => {
        this.saving = false;
        this.showFormModal = false;
        this.loadTemplates();
        this.toast(this.editMode ? 'Template updated successfully ✓' : 'Template created successfully ✓');
      },
      error: (err) => {
        console.error('Save error:', err);
        this.saving = false;
        this.toast('Failed to save template');
      }
    });
  }

  // ── Delete ────────────────────────────────────────────────────────────────
  confirmDelete(t: TemplateDto): void {
    this.templateToDelete = t;
    this.showDeleteModal = true;
  }

  deleteTemplate(): void {
    if (!this.templateToDelete) return;
    this.templateService.delete(this.templateToDelete.id!).subscribe({
      next: () => {
        this.showDeleteModal = false;
        this.templateToDelete = null;
        this.loadTemplates();
        this.toast('Template deleted successfully');
      },
      error: () => this.toast('Failed to delete template')
    });
  }

  // ── Versions ──────────────────────────────────────────────────────────────
  openVersionModal(t: TemplateDto): void {
    this.selectedTemplate = t;
    this.versions = [];
    this.loadingVersions = true;
    this.showVersionModal = true;

    this.templateService.getVersions(t.id!).subscribe({
      next: (v) => {
        this.versions = v;
        this.loadingVersions = false;
      },
      error: () => {
        this.loadingVersions = false;
        this.toast('Failed to load versions');
      }
    });
  }

  closeVersionModal(): void {
    this.showVersionModal = false;
    this.selectedTemplate = null;
    this.versions = [];
  }

  restoreVersion(v: TemplateVersionDto): void {
    if (!this.selectedTemplate) return;
    this.templateService.restoreVersion(this.selectedTemplate.id!, v.id).subscribe({
      next: () => {
        this.closeVersionModal();
        this.loadTemplates();
        this.toast(`Restored to version ${v.version} ✓`);
      },
      error: () => this.toast('Failed to restore version')
    });
  }

  // ── Section/Task Helpers ──────────────────────────────────────────────────
  addSection(): void {
    if (!this.form.sections) this.form.sections = [];
    this.form.sections.push({ sectionName: '', tasks: [] });
  }

  removeSection(si: number): void {
    this.form.sections!.splice(si, 1);
  }

  addTask(si: number): void {
    this.form.sections![si].tasks.push({
      title: '', priority: 'Medium', dueDateDays: 1, assignees: []
    });
  }

  removeTask(si: number, ti: number): void {
    this.form.sections![si].tasks.splice(ti, 1);
  }

  // ── Toast ─────────────────────────────────────────────────────────────────
  private toast(msg: string): void {
    this.toastMessage = msg;
    this.showToast = true;
    setTimeout(() => this.showToast = false, 3000);
  }

  // ── Helpers ───────────────────────────────────────────────────────────────
  private emptyForm(): TemplateDto {
    return {
      templateName: '',
      department: '',
      visibility: 'Public',
      workflowType: 'Sequential',
      description: '',
      sections: []
    };
  }
}