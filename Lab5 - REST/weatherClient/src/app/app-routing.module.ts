import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {CurrentWeatherComponent} from './current-weather/current-weather.component';
import {HourlyWeatherComponent} from './hourly-weather/hourly-weather.component';
import {PageNotFoundComponent} from './page-not-found/page-not-found.component';
import {MainPageComponent} from './main-page/main-page.component';

const routes: Routes = [
  {path: 'current_weather', component: CurrentWeatherComponent},
  {path: 'hourly_weather', component: HourlyWeatherComponent},
  {path: 'main_page', component: MainPageComponent},
  {path: '', redirectTo: '/main_page', pathMatch: 'full'},
  {path: '**', component: PageNotFoundComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
