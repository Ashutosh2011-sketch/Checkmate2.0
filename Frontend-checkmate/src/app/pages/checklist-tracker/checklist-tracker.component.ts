import { Component } from '@angular/core';

interface Task {
  id: number;
  title: string;
  completed: boolean;
  workflowType: 'Sequential' | 'Parallel';
  deadline: string;
  priority: 'High' | 'Medium' | 'Low';
  assignedTo?: string;
  dependsOn?: number;
  comments: string[];
  attachments: string[];
}

interface Checklist {
  name: string;
  department: string;
  tasks: Task[];
  activityLog: string[];
}

@Component({
  selector: 'app-checklist-tracker',
  templateUrl: './checklist-tracker.component.html',
  styleUrls: ['./checklist-tracker.component.css']
})
export class ChecklistTrackerComponent {

  searchText = '';
  statusFilter = 'All';
  currentPage = 1;
  itemsPerPage = 3;

  currentUser = "Krish";

  checklists: Checklist[] = [];

  constructor(){
    this.addDemoChecklist(); // simulate admin created checklist
  }

  addDemoChecklist(){

    const checklist: Checklist = {
      name:'Employee Onboarding',
      department:'HR',
      activityLog:[],
      tasks:[
        {
          id:1,
          title:'Collect Documents',
          completed:false,
          workflowType:'Sequential',
          deadline:'2026-04-01',
          priority:'High',
          assignedTo:'HR Manager',
          comments:[],
          attachments:[]
        },
        {
          id:2,
          title:'System Access Setup',
          completed:false,
          workflowType:'Sequential',
          deadline:'2026-04-05',
          priority:'Medium',
          dependsOn:1,
          comments:[],
          attachments:[]
        },
        {
          id:3,
          title:'Laptop Allocation',
          completed:false,
          workflowType:'Parallel',
          deadline:'2026-04-05',
          priority:'Low',
          comments:[],
          attachments:[]
        }
      ]
    };

    this.checklists.push(checklist);
  }

  getProgress(checklist: Checklist): number {
    const completed = checklist.tasks.filter(t => t.completed).length;
    return Math.round((completed / checklist.tasks.length) * 100);
  }

  getStatus(checklist: Checklist): string {

    const progress = this.getProgress(checklist);

    const hasOverdue = checklist.tasks.some(t =>
      !t.completed && new Date(t.deadline) < new Date()
    );

    if(hasOverdue) return 'Overdue';
    if(progress === 100) return 'Completed';
    if(progress > 0) return 'In Progress';
    return 'Pending';
  }

  toggleTask(checklist:Checklist,task:Task){

    if(task.workflowType === 'Sequential'){
      const prev = checklist.tasks.find(t=>t.id === task.dependsOn);

      if(prev && !prev.completed){
        alert("Complete previous task first");
        return;
      }
    }

    task.completed = !task.completed;

    checklist.activityLog.push(
      `${this.currentUser} marked "${task.title}" as ${task.completed ? 'Completed':'Pending'}`
    );

  }

  addComment(task:Task,comment:string){

    if(!comment) return;

    task.comments.push(`${this.currentUser}: ${comment}`);

  }

  addAttachment(task:Task,file:string){

    if(!file) return;

    task.attachments.push(file);

  }

  claimTask(task:Task){

    if(!task.assignedTo){
      task.assignedTo = this.currentUser;
    }

  }

}
