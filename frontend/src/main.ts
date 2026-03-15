/*
 * FILE: main.ts | LOCATION: frontend/src/
 * PURPOSE: The ENTRY POINT of the Angular frontend application.
 *          This is the first file that runs when the Angular app starts.
 *          It bootstraps (starts up) the root App component using the app config.
 *
 * CONNECTS TO:
 *   - app/app.ts → The root component that contains <router-outlet> and <app-navbar>
 *   - app/app.config.ts → Configuration with router, HTTP client, and interceptor
 */
import { bootstrapApplication } from '@angular/platform-browser';
import { App } from './app/app';               // → app/app.ts (root component)
import { appConfig } from './app/app.config';  // → app/app.config.ts (providers)

// Start the Angular application with the root App component and its configuration
bootstrapApplication(App, appConfig).catch(err => console.error(err));
