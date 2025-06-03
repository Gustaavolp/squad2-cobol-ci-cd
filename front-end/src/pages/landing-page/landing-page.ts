import { ChangeDetectionStrategy, Component } from '@angular/core';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-landing-page',
  standalone: true,
  imports: [MatToolbarModule, MatIconModule, CommonModule],
  templateUrl: './landing-page.html',
  styleUrls: ['./landing-page.scss', ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LandingPage {
}