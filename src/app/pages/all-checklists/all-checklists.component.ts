import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router'; 

export interface ChecklistItem {
  id: number;
  title: string;
  assignee: string;
  progress: number;
  status: string;
  deadline: string;
  priority: string;
}

@Component({
  selector: 'app-checklist-tracker', 
  templateUrl: './all-checklists.component.html',
  styleUrls: ['./all-checklists.component.css']
})
export class AllChecklistComponent implements OnInit {

  allChecklists: ChecklistItem[] = [];
  filteredChecklists: ChecklistItem[] = [];

  currentStatus = 'All';
  currentPriority = 'All';
  searchQuery = '';

  constructor(private router: Router) {}

  ngOnInit() {
    this.allChecklists = [
      {
        id: 1,
        title: 'Employee Onboarding - John Doe',
        assignee: 'Anjali (HR)',
        progress: 65,
        status: 'In Progress',
        deadline: '12 Oct, 2026',
        priority: 'High'
      },
      {
        id: 2,
        title: 'IT Asset Allocation - Q3',
        assignee: 'Pratik (IT)',
        progress: 20,
        status: 'In Progress',
        deadline: '15 Oct, 2026',
        priority: 'Medium'
      },
      {
        id: 3,
        title: 'Quarterly Security Audit',
        assignee: 'Rahul (Admin)',
        progress: 100,
        status: 'Completed',
        deadline: '01 Oct, 2026',
        priority: 'High'
      },
      {
        id: 4,
        title: 'Server Migration & Backup',
        assignee: 'Vikram (DevOps)',
        progress: 85,
        status: 'In Progress',
        deadline: '05 Nov, 2026',
        priority: 'High'
      },
      {
        id: 5,
        title: 'Annual Performance Appraisals',
        assignee: 'Neha (HR)',
        progress: 0,
        status: 'Pending',
        deadline: '20 Dec, 2026',
        priority: 'Medium'
      },
      {
        id: 6,
        title: 'Vendor Contract Renewal',
        assignee: 'Suresh (Legal)',
        progress: 100,
        status: 'Completed',
        deadline: '28 Sep, 2026',
        priority: 'Medium'
      }
    ];
    this.filteredChecklists = [...this.allChecklists];
  }

  onSearch(event: any) {
    this.searchQuery = event.target.value.toLowerCase(); 
    this.applyFilters(); 
  }


  setFilter(filterType: string, value: string) {
    if (filterType === 'status') {
      if(this.currentStatus === value){
        this.currentStatus = 'All';
      }else{
        this.currentStatus = value;
      }
    } 
    else if (filterType === 'priority') {
      if(this.currentPriority === value){
        this.currentPriority = 'All';
      }else{
        this.currentPriority = value;
      }
    }
    
    this.applyFilters();
  }

  applyFilters() {
    this.filteredChecklists = this.allChecklists.filter(item => {
      const matchStatus = this.currentStatus === 'All' || item.status === this.currentStatus;
      
      const matchPriority = this.currentPriority === 'All' || item.priority === this.currentPriority;

      const matchSearch = item.title.toLowerCase().includes(this.searchQuery) || 
       item.assignee.toLowerCase().includes(this.searchQuery);
      
      return matchStatus && matchPriority && matchSearch;
    });
  }

  openDetail(item: ChecklistItem) {
      this.router.navigate(['/checklist-detail', item.id]); 
  }
}
