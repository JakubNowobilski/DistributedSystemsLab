import { Component, OnInit } from '@angular/core';
import {WeatherService} from '../services/weather.service';
import {ActivatedRoute, Router} from '@angular/router';
import {HourlyWeatherResponse} from '../model/hourlyWeatherResponse';

@Component({
  selector: 'app-hourly-weather',
  templateUrl: './hourly-weather.component.html',
  styleUrls: ['./hourly-weather.component.css']
})
export class HourlyWeatherComponent implements OnInit {
  public hourlyWeatherResponse: HourlyWeatherResponse;
  private weatherService: WeatherService;
  private route: ActivatedRoute;
  private router: Router;
  constructor(weatherService: WeatherService, route: ActivatedRoute, router: Router) {
    this.weatherService = weatherService;
    this.route = route;
    this.router = router;
    this.hourlyWeatherResponse = undefined;
  }

  ngOnInit(): void {
    this.route.queryParamMap.subscribe(paramMap => {
      const location = paramMap.get('location');
      const date = paramMap.get('date');
      if (location && date){
        this.weatherService.fetchHourlyWeather(location, date).subscribe(
          (hourlyWeatherResponse: HourlyWeatherResponse) => {
              this.hourlyWeatherResponse = hourlyWeatherResponse;
              console.log(this.hourlyWeatherResponse.hourlyWeather);
            },
            (error => {
              console.log('Error. Error message: ' + error);
            })
          );
      }
    });
  }

  getLocationDescription(): string{
    let description = this.hourlyWeatherResponse.location.adminArea1;
    const adminAreas = [
      this.hourlyWeatherResponse.location.postalCode,
      this.hourlyWeatherResponse.location.adminArea3,
      this.hourlyWeatherResponse.location.adminArea4,
      this.hourlyWeatherResponse.location.adminArea5,
      this.hourlyWeatherResponse.location.adminArea6,
      this.hourlyWeatherResponse.location.street,
    ];
    for (const area of adminAreas){
      if (area){
        description += `, ${area}`;
      }
    }
    description += ` (${this.hourlyWeatherResponse.location.latLng.lat}, `;
    description += `${this.hourlyWeatherResponse.location.latLng.lng})`;
    return description;
  }

  onClearPage(): void{
    this.router.navigate(['/main_page']);
  }
}
