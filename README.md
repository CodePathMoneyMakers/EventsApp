Original App Design Project - README Template
===

# LANDMARK

## Table of Contents
1. [Overview](#Overview)
1. [Product Spec](#Product-Spec)
1. [Wireframes](#Wireframes)
2. [Schema](#Schema)

## Overview
### Description
This app allows the user to create and search for local events. (Ex: Yard sale, house party, meet and greet, hangout session)

### App Evaluation
- **Category:** Events.
- **Mobile:** For the time being, this application is mobile only.
- **Story**: Home feed, search for events, create event, profile view.
- **Market:** This app are for any people looking to join, search, and create events in their local area.
- **Habit:** This app can be used approximately 1-2 times a week or when you want to go to a new event.
- **Scope:** This app aims to become global! Being able to search and find events from all around the world will open greater opportunities for the user!

## Product Spec

### 1. User Stories (Required and Optional)

**Required**

* Account Signup - Users should be able to create an account using their email and password. Users should create a username.
* Account Sign In - Users should have the ability to sign in with their credentials.
* Events Creation - Users will have the ability to create an event with event name, type, description, date, and time. Events should also be able to be made private or public.
* Events Feed - Scrolling view of events occuring in your area
* User profile - Users can post a picture and bio 
* Search by event type 

**Additional**

* Users can select the distance to which their event will be broadcas (ex. available to users 3 miles from me)
* Users can set range in which they find events (ex. search 5 miles near me)
* Users can determine the area they want to search for events in (ex. search events in Boston, Massachussetts)
* Max user limit - stop accepting rsvps after x users
* Integrate pictures within post (ex. photo of house, garage sale, etc.)

### 2. Screen Archetypes

* Login
   * Sign in/Sign out
   * Register 
* Stream 
   * Recycler View showing events in your area
* Google Maps View
   * Showing the location events near you 
* Compose Event
   * Making an event / all its details, private or public
* Event Details
   * RSVP to the event, date and time 

### 3. Navigation

**Tab Navigation** (Tab to Screen)

* Home Feed
* Search
* Add Events 
* User Profile


**Flow Navigation** (Screen to Screen)

* Login Screen
   * => Home Feed
* Registration Screen
   * => Home Feed
* Discover Button
   * => Search
* Events
    * => Details Poster
* Buy Tickets Button
    * => Event Creation
* Event Creation
    * => Home Feed

## Wireframes
<img src="https://github.com/CodePathMoneyMakers/EventsApp/blob/master/WhatsApp%20Image%202021-03-13%20at%203.23.31%20PM.jpeg" width=600>

### [BONUS] Digital Wireframes & Mockups

### [BONUS] Interactive Prototype

## Schema 
[This section will be completed in Unit 9]
### Models
[Add table of models]
### Networking
- [Add list of network requests by screen ]
- [Create basic snippets for each Parse network request]
- [OPTIONAL: List endpoints if using existing API such as Yelp]
