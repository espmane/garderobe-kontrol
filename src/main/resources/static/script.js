
const form = document.querySelector("#outfitForm");
const weatherContainer = document.querySelector("#weather");
const resultSection = document.querySelector("#result");
const recommendation = document.querySelector("#recommendation");
const submitButton = document.querySelector("#submitButton");

async function loadWeather() {
    try {
        const response = await fetch("/api/weather");

        if (!response.ok) {
            throw new Error("Could not fetch weather");
        }

        const weather = await response.json();
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
    temperature.textContent = `${weather.temperature}°C`;

    const description = document.createElement("p");
    description.textContent = weather.description ?? "Dagens vejr";

    const wind = document.createElement("p");
    wind.textContent = `Vind: ${weather.windSpeed} m/s`;

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

function getOutfitData() {
    return {
        activity: getSelectedRadio("activity"),
        dayType: getSelectedRadio("dayType"),
        accessories: getSelectedValues("accessories"),
        baseLayer: getSelectedValues("baseLayer"),
        headwear: getSelectedValues("headwear"),
        jacket: getSelectedValues("jacket"),
        midtLayer: getSelectedValues("midtLayer"),
        pants: getSelectedValues("pants"),
        shirt: getSelectedValues("shirt"),
        shoes: getSelectedValues("shoes"),
        creativeDay: document.querySelector("#creativeDay").checked,
        extraClothes: document.querySelector("#extraClothes").checked
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
        throw new Error("Could not get outfit recommendation");
    }

    return await response.text();
}

function displayRecommendation(result) {
    recommendation.textContent = result;
    resultSection.hidden = false;
}

form.addEventListener("submit", async (event) => {
    event.preventDefault();

    const outfitData = getOutfitData();

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
