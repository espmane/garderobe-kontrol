const form = document.querySelector("#outfitForm");
const weatherContainer = document.querySelector("#weather");
const resultSection = document.querySelector("#result");
const recommendation = document.querySelector("#recommendation");
const submitButton = document.querySelector("#submitButton");
const missingButton = document.querySelector("#submitButton-missing");

const socket = new WebSocket("ws://localhost:8080/ws")

socket.onmessage = () => throwTomato();

function throwTomato() {
    const tomato = document.getElementById("tomato-animation");
    const splat = document.getElementById("tomato-splat");

    tomato.style.display = splat.style.display = "block";
    splat.style.opacity = 0;

    tomato.animate([
        {opacity: 0, transform: "translate(0, 0) scale(.025) rotate(0deg)"},
        {opacity: 1, transform: "translate(-18vw, -12vh) scale(.22) rotate(360deg)", offset: .22},
        {transform: "translate(-35vw, -30vh) scale(2) rotate(950deg)", offset: .58},
        {opacity: 1, transform: "translate(-50vw, -50vh) scale(13) rotate(1620deg)"}
    ], {
        duration: 1350,
        easing: "linear",
        fill: "forwards"
    }).onfinish = () => {
        tomato.style.display = "none";
        splat.style.opacity = 1;

        setTimeout(() => {
            splat.style.opacity = 0;
            setTimeout(() => splat.style.display = "none", 400);
        }, 150);
    };
}
let userLatitude;
let userLongitude;

async function loadWeather() {
    navigator.geolocation.getCurrentPosition(async (position) => {
        try {
            const latitude = position.coords.latitude;
            const longitude = position.coords.longitude;

            userLatitude = latitude;
            userLongitude = longitude;

            const response = await fetch(
                `/api/weather?latitude=${latitude}&longitude=${longitude}`
            );

            if (!response.ok) {
                throw new Error("Could not fetch weather");
            }

            const weather = await response.json();
            displayWeather(weather);

        } catch (error) {
            console.error(error);
            weatherContainer.textContent = "Kunne ikke hente dagens vejr.";
        }
    }, (error) => {
        console.error(error);
        weatherContainer.textContent = "Kunne ikke finde din lokation.";
    });
}

function getDateTimeHour() {

    const date = new Date();

    let hour = date.getHours();

    return (hour < 10 ? "0" : "") + hour
}


function getWeatherIcon(weatherCode) {
    switch (weatherCode) {
        case 0:
            return "☀️";

        case 1:
            return "🌤️";

        case 2:
            return "⛅";

        case 3:
            return "☁️";

        case 45:
        case 48:
            return "🌫️";

        case 51:
        case 53:
        case 55:
            return "🌦️";

        case 56:
        case 57:
            return "🌧️";

        case 61:
        case 63:
        case 65:
        case 66:
        case 67:
            return "🌧️";

        case 71:
        case 73:
        case 75:
        case 77:
            return "🌨️";

        case 80:
        case 81:
        case 82:
            return "🌦️";

        case 85:
        case 86:
            return "🌨️";

        case 95:
        case 96:
        case 99:
            return "⛈️";

        default:
            return "🌤️";
    }
}

function displayWeather(weather) {
    weatherContainer.replaceChildren();
    const hourOfDay = getDateTimeHour();

    const weatherIcon = document.querySelector(
        ".weather-heading .section-icon"
    );

    const weatherCode = weather.daily?.weatherCode?.[0];

    if (weatherIcon) {
        weatherIcon.textContent = getWeatherIcon(weatherCode);
    }

    const weatherInfo = document.createElement("div");
    weatherInfo.className = "weather-info";

    const temperatureContainer = document.createElement("div");
    temperatureContainer.className = "temperature-container";

    const temperature = document.createElement("p");
    temperature.className = "temperature";
    temperature.textContent = `${weather.hourly.temperature[hourOfDay]}°C`; //rent faktisk ulovligt

    const temperatureLabel = document.createElement("p");
    temperatureLabel.className = "temperature-label";
    temperatureLabel.textContent = "Temperatur";

    temperatureContainer.append(temperature, temperatureLabel);

    const weatherDetails = document.createElement("div");
    weatherDetails.className = "weather-details";

    const description = document.createElement("p");
    description.className = "weather-description";
    description.textContent = weather.description ?? "Dagens vejr";

    const wind = document.createElement("p");
    wind.className = "weather-wind";
    wind.textContent = `Vind: ${weather.hourly.windSpeed[hourOfDay]} m/s`; //kriminelt

    weatherDetails.append(description, wind);

    weatherInfo.append(temperatureContainer, weatherDetails);
    weatherContainer.append(weatherInfo);
}

function getSelectedValues(name) {
    const checkboxes = document.querySelectorAll(
        `input[name="${name}"]:checked`
    );

    return Array.from(checkboxes).map(
        checkbox => checkbox.value
    );
}

function getSelectedRadio(name) {
    const selected = document.querySelector(
        `input[name="${name}"]:checked`
    );

    return selected ? selected.value : null;
}

function buildOutfitRequest() {
    return {
        latitude: userLatitude,
        longitude: userLongitude,
        mobilityLevel: getSelectedRadio("mobilityLevel"),
        outdoorTime: getSelectedRadio("outdoorTime"),
        dailyActivities: getSelectedValues("dailyActivities"),
        availableClothes: getSelectedOutfit()
    };
}

function getSelectedOutfit() {
    return {
        headwear: getSelectedValues("headwear"),
        accessories: getSelectedValues("accessories"),
        baseLayer: getSelectedValues("baseLayer"),
        midLayer: getSelectedValues("midLayer"),
        outerLayer: getSelectedValues("outerLayer"),
        pants: getSelectedValues("pants"),
        shoes: getSelectedValues("shoes")
    };
}

async function getOutfitRecommendation(outfitData, endpoint) {
    console.log("JSON til backend:", JSON.stringify(outfitData, null, 2));

    const response = await fetch(endpoint, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(outfitData)
    });

    if (!response.ok) {
        throw new Error("Could not get outfit recommendation");
    }

    return await response.json();
}

function formatEnum(value) {
    const translations = {
        CAP: "Kasket",
        BEANIE: "Hue",
        BUCKET_HAT: "Bøllehat",
        SUN_HAT: "Solhat",
        BALACLAVA: "Elefanthue",
        HEADBAND: "Pandebånd",

        NECK_WARMER: "Halsedisse",
        MITTENS: "Luffer",
        GLOVES: "Handsker",
        SUN_PROTECTION: "Solbeskyttelse",

        BODY: "Body",
        UNDERWEAR: "Undertøj",
        TIGHTS: "Strømpebukser/Leggings",
        SOCKS: "Strømper",
        WOOL_BASE_LAYER: "Uldundertøj",

        T_SHIRT: "T-shirt",
        LONG_SLEEVE_SHIRT: "Langærmet t-shirt",
        SWEATER: "Sweater",
        FLEECE: "Fleece",
        THERMAL_SHIRT: "Termotrøje",

        RAIN_JACKET: "Regnjakke",
        WINTER_JACKET: "Vinterjakke",
        LIGHT_JACKET: "Let jakke",
        THERMAL_JACKET: "Termojakke",
        SOFTSHELL_JACKET: "Softshelljakke",
        RAIN_PANTS: "Regnbukser",
        THERMAL_PANTS: "Termobukser",
        SNOWSUIT: "Flyverdragt",

        SHORTS: "Shorts",
        REGULAR_PANTS: "Almindelige bukser",
        LEGGINGS: "Leggings",

        SNEAKERS: "Sneakers",
        RAIN_BOOTS: "Gummistøvler",
        WINTER_BOOTS: "Vinterstøvler",
        SANDALS: "Sandaler",
        THERMAL_BOOTS: "Termostøvler",
        INDOOR_SHOES: "Indesko"
    };

    return translations[value] ?? value;
}


function displayRecommendation(result, showExtraClothes = true) {
    recommendation.replaceChildren();
    const categories = {
        headwear: "Hovedbeklædning",
        accessories: "Tilbehør",
        baseLayer: "Inderste lag",
        midLayer: "Mellemlag",
        outerLayer: "Overtøj",
        pants: "Bukser",
        shoes: "Fodtøj"
    };

    function displayOutfit(outfit, title) {
        const items = [];

        for (const key in categories) {
            const categoryItems = outfit?.[key] ?? [];

            if (categoryItems.length > 0) {
                items.push({
                    category: categories[key],
                    clothes: categoryItems.map(formatEnum).join(", ")
                });
            }
        }

        if (items.length === 0) {
            return false;
        }

        const heading = document.createElement("h3");
        heading.textContent = title;

        const list = document.createElement("ul");

        items.forEach(item => {
            const listItem = document.createElement("li");
            listItem.textContent = `${item.category}: ${item.clothes}`;
            list.appendChild(listItem);
        });

        recommendation.append(heading, list);

        return true;
    }

    const hasMissingClothes = displayOutfit(
        result.recommendedOutfit,
        "Tøj, der ikke er registreret i garderoben"
    );

    if (!hasMissingClothes) {
        const message = document.createElement("p");
        message.textContent =
            "Den registrerede garderobe ser ud til at dække barnets behov for denne dag.";

        recommendation.appendChild(message);
    }

    if (showExtraClothes) {
        const hasExtraClothes = displayOutfit(
            result.extraClothesToPack,
            "Ekstra skiftetøj at medbringe"
        );

        if (!hasExtraClothes) {
            const message = document.createElement("p");
            message.textContent =
                "Der vurderes ikke at være behov for ekstra skiftetøj.";

            recommendation.appendChild(message);
        }
    }

    const explanation = document.createElement("p");
    explanation.textContent = result.explanation ?? "";

    recommendation.appendChild(explanation);

    resultSection.hidden = false;
}

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const clickedButton = event.submitter;

    let endpoint;
    let showExtraClothes;

    if (clickedButton.id === "submitButton") {
        endpoint = "/api/outfit/recommend";
        showExtraClothes = false;

    } else if (clickedButton.id === "submitButton-missing") {
        endpoint = "/api/outfit/missing";
        showExtraClothes = true;

    } else {
        return;
    }

    const outfitData = buildOutfitRequest();

    if (userLatitude == null || userLongitude == null) {
        recommendation.textContent =
            "Vi kunne ikke finde din lokation. Prøv igen.";

        resultSection.hidden = false;
        return;
    }

    if (!outfitData.mobilityLevel || !outfitData.outdoorTime) {
        resultSection.hidden = false;
        recommendation.textContent =
            "Vælg barnets motoriske niveau og tid indendørs/udendørs.";
        return;
    }

    submitButton.disabled = true;
    missingButton.disabled = true;

    clickedButton.textContent = "Finder passende tøj...";

    resultSection.hidden = false;

    recommendation.textContent =
        "Vi finder de beklædningsdele, der er relevante for dit barns dag...";

    try {
        const result = await getOutfitRecommendation(
            outfitData,
            endpoint
        );

        displayRecommendation(result, showExtraClothes);

    } catch (error) {
        console.error(error);
        recommendation.textContent =
            "Der opstod en fejl. Kunne ikke hente tøjanbefalingen.";

    } finally {
        submitButton.disabled = false;
        missingButton.disabled = false;

        submitButton.innerHTML =
            "<span>♧</span> Find passende tøj <span>→</span>";

        missingButton.innerHTML =
            "<span>♧</span> Find manglende tøj <span>→</span>";
    }
});

loadWeather();
