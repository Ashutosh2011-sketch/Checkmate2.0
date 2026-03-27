import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormArray, Validators } from '@angular/forms';
import { CdkDragDrop, moveItemInArray } from '@angular/cdk/drag-drop';
import { TEMPLATE_LIBRARY, type ChecklistTemplateValue } from './template-library';
import { ChecklistService } from 'src/app/core/services/checklist.service';






@Component({
  selector: 'app-checklist-builder',
  templateUrl: './checklist-builder.component.html',
  styleUrls: ['./checklist-builder.component.css']
})
export class ChecklistBuilderComponent implements OnInit {
  checklistForm!: FormGroup;

  departments = ['HR', 'IT', 'Engineering', 'Finance'];
  availableUsers = ['John (IT)', 'Sarah (HR)', 'Admin', 'Vikram (DevOps)'];
  availableTasksForDependency: string[] = []; 
  selectedTemplate: string = 'blank';

  constructor(
    private fb: FormBuilder,
    private checklistService: ChecklistService
  ) {}


  ngOnInit(): void {
    this.checklistForm = this.fb.group({
      checklistName: ['', Validators.required],
      department: ['', Validators.required],
      visibility: ['Private', Validators.required], 
      workflowType: ['Sequential', Validators.required], 
      sections: this.fb.array([]) 
    });

    this.addSection();
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

    if (!templateName || templateName === 'blank') {
      this.checklistForm.patchValue({
        checklistName: '',
        department: '',
        visibility: 'Private',
        workflowType: 'Sequential'
      });
      this.addSection();
      this.updateDependenciesList();
      return;
    }

    const template = TEMPLATE_LIBRARY[templateName];
    if (!template) {
      this.addSection();
      this.updateDependenciesList();
      return;
    }

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

  // onSubmit(): void {
  //   console.log('Final Checklist Data:', this.checklistForm.value);
  //   alert('Checklist Created Successfully! Check console for data.');
  // }

  onSubmit(): void {
    console.log('Sending data to backend...', this.checklistForm.value);
    
    this.checklistService.createChecklist(this.checklistForm.value)
      .subscribe({
        next: (response: any) => {
          console.log('Database saved response:', response);
          alert('🔥 Checklist Successfully Saved in PostgreSQL Database!');
          this.checklistForm.reset();
        },
        error: (error: any) => {
          console.error('Error saving to database:', error);
          alert('Kuch gadbad hui, console check karo!');
        }

      });
  }
}
