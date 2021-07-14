import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit{
  private router: Router;
  private formBuilder: FormBuilder;
  public currentWeatherForm: FormGroup;
  public hourlyWeatherForm: FormGroup;
  title = 'weatherClient';

  constructor(router: Router, formBuilder: FormBuilder) {
    this.router = router;
    this.formBuilder = formBuilder;
  }

  ngOnInit(): void {
    this.currentWeatherForm = this.formBuilder.group({
      location: ['', Validators.required]
    });
    this.hourlyWeatherForm = this.formBuilder.group({
      location2: ['', Validators.required],
      date2: ['', Validators.required]
    });
  }

  navigateCurrentWeather(): void{
    const location = this.currentWeatherForm.controls.location.value;
    this.router.navigate(['/current_weather'],
      {queryParams: {location: location}});
    this.currentWeatherForm.reset();
  }

  navigateHourlyWeather(): void{
    const location2 = this.hourlyWeatherForm.controls.location2.value;
    const date2 = this.hourlyWeatherForm.controls.date2.value;
    this.router.navigate(['/hourly_weather'],
      {queryParams: {location: location2, date: date2}});
    this.hourlyWeatherForm.reset();
  }
}
