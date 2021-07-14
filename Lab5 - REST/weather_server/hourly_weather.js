const C = require("./constants.js")
const axios = require("axios");

function fetchHourlyWeather(lat, lng, date){
    return axios.get(`${compareDate(date) ? C.weatherApiForecastURL: C.weatherApiHistoryURL}?key=${C.weatherApiAPI_KEY}&q=${lat},${lng}&dt=${date}`)
    .then(result => {
        const forecastday = result.data?.forecast?.forecastday[0]
        let hourlyWeather = new C.HourlyWeather()
        hourlyWeather.maxTemperature = forecastday?.day?.maxtemp_c
        hourlyWeather.minTemperature = forecastday?.day?.mintemp_c
        hourlyWeather.avgTemperature = forecastday?.day?.avgtemp_c
        hourlyWeather.conditionText = forecastday?.day?.condition?.text
        hourlyWeather.conditionIcon = "http://" + forecastday?.day?.condition?.icon.substring(2)
        hourlyWeather.sunrise = forecastday?.astro?.sunrise
        hourlyWeather.sunset = forecastday?.astro?.sunset
        hourlyWeather.date = forecastday?.date
        hourlyWeather.hours = forecastday?.hour?.map(hour => {
            let hourData = new C.HourData()
            hourData.time = extractTime(hour.time)
            hourData.temperature = hour.temp_c
            hourData.conditionText = hour.condition?.text
            hourData.conditionIcon = "http://" + hour.condition?.icon.substring(2)
            hourData.precipitationIntensity = hour.precip_mm
            return hourData
        })
        return hourlyWeather
    })
    .catch(error => {
        console.log(error)
        return {error: error}
    })
}

function extractTime(data){
    return data.substring(data.indexOf(" ") + 1);
}

function compareDate(providedDate){
    const currentDate = new Date()
    const currentMonth = currentDate.getMonth() + 1
    const currentDay = currentDate.getDate()
    const firstHyp = providedDate.indexOf("-") + 1
    const secondHyp = providedDate.indexOf("-", firstHyp) + 1
    const providedMonth = parseInt(providedDate.substring(firstHyp, secondHyp - 1))
    const providedDay = parseInt(providedDate.substring(secondHyp))
    return currentMonth < providedMonth || (currentMonth === providedMonth && currentDay <= providedDay)
}

module.exports = fetchHourlyWeather
