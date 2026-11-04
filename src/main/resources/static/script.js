
const form = document.querySelector("#outfitForm");
const weatherContainer = document.querySelector("#weather");
const resultSection = document.querySelector("#result");
const recommendation = document.querySelector("#recommendation");
const submitButton = document.querySelector("#submitButton");
let userLocation = null;

function getUserLocation() {
    return new Promise((resolve, reject) => {
        if (!navigator.geolocation) {
            reject(new Error("Browseren understøtter ikke lokationen."));
            return;
        }

        navigator.geolocation.getCurrentPosition(
            (position) => {
                userLocation = {
                    latitude: position.coords.latitude,
                    longitude: position.coords.longitude
                };

                resolve(userLocation);
                },

            (error) => {
                reject(error);
            }
        );
    });
}

async function loadWeather() {
    try {
        const location = await getUserLocation();

        const response = await fetch(
            `/api/weather?latitude=${location.latitude}&longitude=${location.longitude}`
        );

        if (!response.ok) {
            throw new Error("Could not fetch weather");
        }

        const weather = await response.json();

        console.log("Vejrdata fra backend:", weather);

        displayWeather(weather);

    } catch (error) {
        console.error(error);
        weatherContainer.textContent = "Kunne ikke hente dagens vejr.";
    }
}

function displayWeather(weather) {
    weatherContainer.replaceChildren();

    const weatherInfo = document.createElement("div");
    weatherInfo.className = "weather-info";

    const details = document.createElement("div");

    const temperature = document.createElement("p");
    temperature.className = "temperature";
    temperature.textContent = `${weather.hourly.temperature[0]}°C`;

    const description = document.createElement("p");

    const weatherCode = weather.daily.weatherCode[0];
    description.textContent = describeWeather(weatherCode);

    const wind = document.createElement("p");
    wind.textContent = `Vind: ${weather.hourly.windSpeed[0]} m/s`;

    details.append(temperature, description, wind);
    weatherInfo.append(details);
    weatherContainer.append(weatherInfo);
}

function describeWeather(code) {
    const descriptions = {
        0: "Klart",
        1: "Let skyet",
        2: "Delvist skyet",
        3: "Overskyet",
        45: "Tåge",
        48: "Tåge",
        51: "Støvregn",
        53: "Støvregn",
        55: "Støvregn",
        61: "Regn",
        63: "Regn",
        65: "Kraftig regn",
        71: "Sne",
        73: "Sne",
        75: "Kraftig sne",
        95: "Tordenvejr"
    };

    return descriptions[code] ?? "Dagens vejr";
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

function getAvailableClothes() {
    const categories = {
        accessories: "ACCESSORIES",
        baseLayer: "BASE_LAYER",
        headwear: "HEADWEAR",
        jacket: "JACKET",
        midtLayer: "MIDT_LAYER",
        pants: "PANTS",
        shoes: "SHOES"
    };

    const clothes = [];

    for (const [name, category] of Object.entries(categories)) {
        getSelectedValues(name).forEach(item => {
            clothes.push({
                category: category,
                item: item
            });
        });
    }

    getSelectedValues("shirt").forEach(item => {
        if (item === "SWEATER") {
            clothes.push({
                category: "MIDT_LAYER",
                item: item
            });
        } else {
            clothes.push({
                category: "SHIRT",
                item: item
            });
        }
    });

    return clothes;
}


function getOutfitData() {
    return {
        latitude: userLocation.latitude,
        longitude: userLocation.longitude,
        date: new Date().toISOString().split("T")[0],
        activity: getSelectedRadio("activity"),
        dayType: getSelectedRadio("dayType"),
        availableClothes: getAvailableClothes()
    };
}

async function getOutfitRecommendation(outfitData) {
    const response = await fetch("/api/outfit", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(outfitData)
    });

    if (!response.ok) {
        const errorText = await response.text();
        console.error("Fejl fra backend:", errorText);
        throw new Error("Could not get outfit recommendation");
    }

    return await response.json();
}

function displayRecommendation(result) {
    recommendation.replaceChildren();

    const explanation = document.createElement("p");
    explanation.textContent = result.explanation;
    recommendation.append(explanation);

    result.recommendedClothes.forEach(clothing => {
        const item = document.createElement("p");
        item.textContent = `${clothing.item}: ${clothing.reason}`;
        recommendation.append(item);
    });

    resultSection.hidden = false;
}

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const activity = getSelectedRadio("activity");
    const dayType = getSelectedRadio("dayType");

    if (!activity || !dayType) {
        resultSection.hidden = false;
        recommendation.textContent =
            "Vælg aktivitetsniveau og om barnet skal være inde eller ude.";
        return;
    }

    submitButton.disabled = true;
    submitButton.textContent = "Finder passende tøj...";

    resultSection.hidden = false;
    recommendation.textContent =
        "Vi finder den bedste påklædning til dit barn...";

    try {
        if (!userLocation) {
            await getUserLocation();
        }

        const outfitData = getOutfitData();

        console.log("Data sendt til backend:", outfitData);

        const result = await getOutfitRecommendation(outfitData);
        displayRecommendation(result);

    } catch (error) {
        console.error(error);
        recommendation.textContent =
            "Kunne ikke hente anbefaling. Prøv igen.";

    } finally {
        submitButton.disabled = false;
        submitButton.textContent = "Find passende tøj";
    }
});

loadWeather();
