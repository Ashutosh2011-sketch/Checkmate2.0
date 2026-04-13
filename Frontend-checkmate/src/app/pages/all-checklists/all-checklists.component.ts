import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router'; 
import { ChecklistService } from '../../core/services/checklist.service';
import { ChecklistSummary } from '../../core/models/checklist.model';

interface ChecklistItem {
  id: number;
  title: string;
  assignee: string;
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
  loading = true;

  currentStatus = 'All';
  currentPriority = 'All';
  searchQuery = '';

  constructor(
    private router: Router,
    private checklistService: ChecklistService
  ) {}

  ngOnInit() {
    this.loading = true;
    this.checklistService.getAllChecklists().subscribe({
      next: (summaries: ChecklistSummary[]) => {
        this.allChecklists = summaries.map(summary => ({
          id: summary.id,
          title: summary.title,
          assignee: summary.assignee,
          status: summary.status,
          deadline: summary.deadline,
          priority: summary.priority
        }));
        this.filteredChecklists = [...this.allChecklists];
        this.loading = false;
      },
  error: (err: any) => {
        console.error('Error loading checklists:', err);
        this.loading = false;
      }
    });
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

  openDetail(item: any) {
      this.router.navigate(['/admin/checklist-detail', item.id]); 
  }
}
