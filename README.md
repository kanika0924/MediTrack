# MediTrack — Java web application

Requirements: JDK 17, Maven 3.9+, VS Code (optional), Chrome. No Android emulator required.

1. Extract the ZIP and open its `meditrack` folder in VS Code.
2. In the VS Code terminal run `mvn spring-boot:run` (first run downloads dependencies, so internet is needed).
3. Open `http://localhost:8080` in Chrome.
4. Add medicines, record doses, restock, save blood tests and open nearby-care searches.
5. To show a phone layout in Chrome press F12, then Ctrl+Shift+M, and select a mobile viewport.
6. Capture real screenshots of your running app for your submission and replace illustrative figures in the report.
7. Stop the server with Ctrl+C. Data is stored in `data/meditrack.mv.db`.

Limitations: Browser reminder checks run only while the page is open. Nearby care opens OpenStreetMap searches and does not verify medicine availability. There is no authentication or encryption; use fictional demonstration data only. No lab booking, medical advice or actual pharmacy purchasing is provided. The report's test plan must be executed on your machine before claiming passing results.
