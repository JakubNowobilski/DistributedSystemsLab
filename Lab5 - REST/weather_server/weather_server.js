const C = require("./constants.js")
const fetchCoords = require("./geocoding_client.js")
const fetchCurrentWeather = require("./current_weather.js")
const fetchHourlyWeather = require("./hourly_weather.js")
const express = require("express");
const cors = require("cors");
const port = 8080;
const app = express();

app.use(cors())
app.use(express.json());
app.use(express.urlencoded({extended: true}))

app.get("/current_weather", ((req, res) => {
    printReqSummary(req)
    const providedLocation = req.query.location;
    if(providedLocation !== undefined){
        fetchCoords(providedLocation)
        .then(result => {
            const lat = result?.latLng?.lat
            const lng = result?.latLng?.lng
            let currentWeatherResponse = new C.CurrentWeatherResponse()
            currentWeatherResponse.providedLocation = providedLocation
            currentWeatherResponse.date = new Date()
            currentWeatherResponse.location = result
            fetchCurrentWeather(lat, lng).then(result => {
                currentWeatherResponse.currentWeather = result
                res.status(200).send(currentWeatherResponse);
            })
        })
        .catch(error => {
            console.log(error)
            res.status(404).send({error: error});
        })
    }else{
        res.status(400).send({error: "Invalid request. Required syntax: /current_weather?location={location}"});
    }
}))

app.get("/hourly_weather", ((req, res) => {
    printReqSummary(req)
    const providedLocation = req.query.location;
    const providedDate = req.query.date;
    const re = new RegExp("^\\d{4}-\\d{1,2}-\\d{1,2}$");
    if(providedLocation && providedDate && re.test(providedDate)){
        fetchCoords(providedLocation)
            .then(result => {
                const lat = result?.latLng?.lat
                const lng = result?.latLng?.lng
                let hourlyWeatherResponse = new C.HourlyWeatherResponse()
                hourlyWeatherResponse.providedLocation = providedLocation
                hourlyWeatherResponse.providedDate = providedDate
                hourlyWeatherResponse.date = new Date()
                hourlyWeatherResponse.location = result
                fetchHourlyWeather(lat, lng, providedDate).then(result => {
                    hourlyWeatherResponse.hourlyWeather = result
                    res.status(200).send(hourlyWeatherResponse);
                })
            })
            .catch(error => {
                console.log(error)
                res.status(404).send({error: error});
            })
    }else{
        res.status(400).send({error: "Invalid request. Required syntax: /hourly_weather?location={location}&date={YYYY-MM-DD}"});
    }
}))

app.get("/*", ((req, res) => {
    printReqSummary(req)
    res.status(200).send({hello_world: "OK"});
}))

app.listen(port, () => {
    console.log("App server listening at http://localhost:" + port);
})

function printReqSummary(req) {
    console.log("Handling " + req.method + " " + decodeURI(req.originalUrl));
}
