export type ChecklistTemplateValue = {
  checklistName: string;
  department: string;
  visibility: 'Public' | 'Private' | string;
  workflowType: 'Sequential' | 'Parallel' | string;
  sections: Array<{
    sectionName: string;
    tasks: Array<{
      title: string;
      description: string;
      assignees: string[];
      priority: 'High' | 'Medium' | 'Low' | string;
      dueDateDays: number;
      dependsOn: string;
      conditionDependentOn: string;
      conditionExpectedOutcome: string;
      remindBefore: number;
      escalateTo: string;
      showAdvanced: boolean;
    }>;
  }>;
};

const defaultTask = (
  title: string,
  overrides?: Partial<ChecklistTemplateValue['sections'][number]['tasks'][number]>
): ChecklistTemplateValue['sections'][number]['tasks'][number] => ({
  title,
  description: '',
  assignees: [],
  priority: 'Medium',
  dueDateDays: 1,
  dependsOn: 'None',
  conditionDependentOn: 'None',
  conditionExpectedOutcome: 'Pass',
  remindBefore: 1,
  escalateTo: 'Manager',
  showAdvanced: false,
  ...(overrides ?? {})
});

export const TEMPLATE_LIBRARY: Record<string, ChecklistTemplateValue> = {
  employeeOnboarding: {
    checklistName: 'Employee Onboarding',
    department: 'HR',
    visibility: 'Private',
    workflowType: 'Sequential',
    sections: [
      {
        sectionName: 'Pre-Joining',
        tasks: [
          defaultTask('Send Offer Letter', { assignees: ['Sarah (HR)'] }),
          defaultTask('Collect Documents', { assignees: ['Sarah (HR)', 'Admin'] }),
          defaultTask('Background Verification', { assignees: ['Admin'] })
        ]
      },
      {
        sectionName: 'Day 1 Setup',
        tasks: [
          defaultTask('Laptop Allocation', { assignees: ['John (IT)', 'Vikram (DevOps)'] }),
          defaultTask('Email Account Creation', { assignees: ['John (IT)'] }),
          defaultTask('System Access Setup', { assignees: ['John (IT)', 'Vikram (DevOps)'] })
        ]
      },
      {
        sectionName: 'Orientation',
        tasks: [
          defaultTask('HR Orientation', { assignees: ['Sarah (HR)'] }),
          defaultTask('Team Introduction', { assignees: ['Admin'] }),
          defaultTask('Project Assignment', { assignees: ['Admin'] })
        ]
      }
    ]
  },
  vendorOnboarding: {
    checklistName: 'Vendor Onboarding',
    department: 'Finance',
    visibility: 'Private',
    workflowType: 'Sequential',
    sections: [
      {
        sectionName: 'Vendor Registration',
        tasks: [
          defaultTask('Vendor Information Submission', { assignees: ['Admin'] }),
          defaultTask('Legal Entity Verification', { assignees: ['Admin'] }),
          defaultTask('Business License Verification', { assignees: ['Admin'] })
        ]
      },
      {
        sectionName: 'Compliance & Documentation',
        tasks: [
          defaultTask('NDA Agreement Signed', { assignees: ['Admin'] }),
          defaultTask('Tax Documents Submitted', { assignees: ['Admin'] })
        ]
      },
      {
        sectionName: 'Finance Setup',
        tasks: [
          defaultTask('Payment Terms Approval', { assignees: ['Admin'] }),
          defaultTask('Finance Approval', { assignees: ['Admin'] })
        ]
      }
    ]
  }
};

