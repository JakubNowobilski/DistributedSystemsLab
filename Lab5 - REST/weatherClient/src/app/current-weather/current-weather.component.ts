import { Component, OnInit } from '@angular/core';
import {WeatherService} from '../services/weather.service';
import {ActivatedRoute, Router} from '@angular/router';
import {CurrentWeatherResponse} from '../model/CurrentWeatherResponse';

@Component({
  selector: 'app-current-weather',
  templateUrl: './current-weather.component.html',
  styleUrls: ['./current-weather.component.css']
})
export class CurrentWeatherComponent implements OnInit {
  public currentWeatherResponse: CurrentWeatherResponse;
  private weatherService: WeatherService;
  private route: ActivatedRoute;
  private router: Router;
  constructor(weatherService: WeatherService, route: ActivatedRoute, router: Router) {
    this.weatherService = weatherService;
    this.route = route;
    this.router = router;
    this.currentWeatherResponse = undefined;
  }

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(paramMap => {
      const location = paramMap.get('location');
      if (location){
        this.weatherService.fetchCurrentWeather(location).subscribe(
          (currentWeatherResponse: CurrentWeatherResponse) => {
            this.currentWeatherResponse = currentWeatherResponse;
          },
          (error => {
            console.log('Error. Error message: ' + error);
          })
        );
      }
    });
  }

  getLocationDescription(): string{
    let description = this.currentWeatherResponse.location.adminArea1;
    const adminAreas = [
      this.currentWeatherResponse.location.postalCode,
      this.currentWeatherResponse.location.adminArea3,
      this.currentWeatherResponse.location.adminArea4,
      this.currentWeatherResponse.location.adminArea5,
      this.currentWeatherResponse.location.adminArea6,
      this.currentWeatherResponse.location.street,
    ];
    for (const area of adminAreas){
      if (area){
        description += `, ${area}`;
      }
    }
    description += ` (${this.currentWeatherResponse.location.latLng.lat}, `;
    description += `${this.currentWeatherResponse.location.latLng.lng})`;
    return description;
  }

  onClearPage(): void{
    this.router.navigate(['/main_page']);
  }
}
