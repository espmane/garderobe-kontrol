const form = document.querySelector("#outfitForm");
const weatherContainer = document.querySelector("#weather");
const resultSection = document.querySelector("#result");
const recommendation = document.querySelector("#recommendation");
const submitButton = document.querySelector("#submitButton");
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

function displayWeather(weather) {
    weatherContainer.replaceChildren();

    const weatherInfo = document.createElement("div");
    weatherInfo.className = "weather-info";

    const details = document.createElement("div");

    const temperature = document.createElement("p");
    temperature.className = "temperature";
    temperature.textContent = `${weather.hourly.temperature[0]}°C`;

    const description = document.createElement("p");
    description.textContent = weather.description ?? "Dagens vejr";

    const wind = document.createElement("p");
    wind.textContent = `Vind: ${weather.hourly.windSpeed[0]} m/s`;

    details.append(temperature, description, wind);
    weatherInfo.append(details);
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
        activity: getSelectedRadio("activity"),
        dayType: getSelectedRadio("dayType"),
        creativeDay: document.querySelector("#creativeDay").checked,
        extraClothes: document.querySelector("#extraClothes").checked,
        availableClothes: getSelectedOutfit()
    };
}

function getSelectedOutfit() {
    return {
        headwear: getSelectedValues("headwear"),
        accessories: getSelectedValues("accessories"),
        baseLayer: getSelectedValues("baseLayer"),
        midtLayer: getSelectedValues("midtLayer"),
        jacket: getSelectedValues("jacket"),
        shirt: getSelectedValues("shirt"),
        pants: getSelectedValues("pants"),
        shoes: getSelectedValues("shoes")
    };
}

async function getOutfitRecommendation(outfitData) {
    console.log("JSON til backend:", JSON.stringify(outfitData, null, 2));
    const response = await fetch("/api/outfit", {
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
        TIGHTS: "Strømpebukser",
        SOCKS: "Strømper",
        WOOL_BASE_LAYER: "Uldundertøj",

        SHIRT: "Trøje",
        SWEATER: "Sweater",
        FLEECE: "Fleece",
        THERMAL_CLOTHING: "Termotøj",

        RAIN_JACKET: "Regnjakke",
        WINTER_JACKET: "Vinterjakke",
        LIGHT_JACKET: "Let jakke",
        THERMAL_JACKET: "Termojakke",

        T_SHIRT: "T-shirt",
        LONG_SLEEVE: "Langærmet trøje",

        SHORTS: "Shorts",
        REGULAR_PANTS: "Almindelige bukser",
        RAIN_PANTS: "Regnbukser",
        THERMAL_PANTS: "Termobukser",

        SNEAKERS: "Sneakers",
        RAIN_BOOTS: "Gummistøvler",
        WINTER_BOOTS: "Vinterstøvler",
        SANDALS: "Sandaler",
        THERMAL_BOOTS: "Termostøvler",
        INDOOR_SHOES: "Indesko"
    };

    return translations[value] ?? value;
}

function displayRecommendation(result) {
    recommendation.replaceChildren();

    const outfit = result.recommendedOutfit;
    const categories = {
        headwear: "Hovedbeklædning",
        accessories: "Tilbehør",
        baseLayer: "Inderste lag",
        midtLayer: "Mellemlag",
        jacket: "Jakke",
        shirt: "Trøje",
        pants: "Bukser",
        shoes: "Sko"
    };

    const heading = document.createElement("h2");
    heading.textContent = "Anbefalet outfit";

    const list = document.createElement("ul");

    for (const key in categories) {
        const item = document.createElement("li");
        if (outfit[key].length === 0) {
            item.textContent = `${categories[key]}: ingenting`;
        } else {
            item.textContent = `${categories[key]}: ${outfit[key].map(formatEnum).join(", ")}`;
        }
        list.appendChild(item);
    }

    const explanation = document.createElement("p");
    explanation.textContent = result.explanation;

    recommendation.append(heading, explanation, list);
    resultSection.hidden = false;
}

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const outfitData = buildOutfitRequest();

    if (userLatitude == null || userLongitude == null) {
        recommendation.textContent = "Vi kunne ikke finde din lokation. Prøv igen.";
        return;
    }

    if (!outfitData.activity || !outfitData.dayType) {
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
        const result = await getOutfitRecommendation(outfitData);
        displayRecommendation(result);

    } catch (error) {
        console.error(error);
        recommendation.textContent =
            "Der opstod en fejl. Kunne ikke hente tøjanbefalingen.";

    } finally {
        submitButton.disabled = false;
        submitButton.textContent = "Find passende tøj";
    }
});

loadWeather();
