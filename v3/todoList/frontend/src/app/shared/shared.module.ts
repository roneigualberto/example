import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { Ng2BootstrapModule, PopoverModule } from 'ng2-bootstrap/ng2-bootstrap';
import { SecurityModule } from '@demoiselle/security';

import { NotificationService } from './notification.service';
import { LoginService } from '../login/shared/login.service';
import { UserDropdownComponent } from './top-nav/user-dropdown.component';
import { SidebarMenuComponent } from './sidebar-menu/sidebar-menu.component';
import { TopNavComponent } from './top-nav/top-nav.component';

import { ErrorFieldComponent } from './components/error-field.component';
import { ErrorFormComponent } from './components/error-form.component';

import { CalendarModule } from 'primeng/primeng';
import { InputTextModule } from 'primeng/primeng';
import { DropdownModule } from 'primeng/primeng';
import '../../../node_modules/primeng/resources/themes/omega/theme.css';
import '../../../node_modules/primeng/resources/primeng.min.css';
import '../../../node_modules/font-awesome/css/font-awesome.min.css';

@NgModule({
  imports: [
    CommonModule,
    RouterModule,
    SecurityModule,
    Ng2BootstrapModule.forRoot(),
    PopoverModule.forRoot(),
    CalendarModule,
    InputTextModule,
    DropdownModule
  ],
  declarations: [SidebarMenuComponent, TopNavComponent, UserDropdownComponent, ErrorFieldComponent, ErrorFormComponent],
  providers: [
    NotificationService,
    LoginService
  ],
  exports: [
    CommonModule,
    FormsModule,
    RouterModule,
    Ng2BootstrapModule,
    SecurityModule,

    SidebarMenuComponent,
    TopNavComponent,
    UserDropdownComponent,
    ErrorFieldComponent,
    ErrorFormComponent,

    CalendarModule,
    InputTextModule,
    DropdownModule
  ]
})
export class SharedModule { }
