# 💎 Jetpack Compose In-App Billing (One-Time & Subscriptions)

A **clean, modular, and production-ready** implementation of **Google Play Billing** supporting both **one-time purchases** and **subscriptions**, built with **Jetpack Compose**, **Kotlin Coroutines**, and **Koin**.

This repository demonstrates how to integrate **Google Play Billing v8+** in a **MVVM + Clean Architecture** setup — using **UseCases**, **ViewModel**, and **Flow-based reactive APIs** for a maintainable and scalable in-app billing solution.

---

## ✨ Features

- 💳 **Supports One-Time Purchases & Subscriptions**
- 🔁 **Automatic Billing Lifecycle Management** (connect, retry, disconnect)
- 🧠 **MVVM + UseCase Architecture**
- 🧱 **Clean Architecture Layers** (Domain, Data, Presentation)
- ⚡ **Coroutine + Flow-based Reactive API**
- 🔄 **Handles purchase acknowledgment & consumption**
- 📦 **Dependency Injection with Koin**
- 🔔 **Retry logic for connection failures**
- 🔍 **Query product details & active purchases**

---

## 🧩 Architecture Overview

### 🧱 Clean Separation of Concerns

- **Repository:** Handles all Google Play BillingClient operations  
- **UseCases:** Encapsulate one billing responsibility each  
- **ViewModel:** Orchestrates billing logic and exposes state to UI  
- **UI (Compose):** Reactively observes billing states  

---

## 🛠️ Tech Stack

| Layer         | Library / Framework     |
|---------------|--------------------------|
| UI            | Jetpack Compose          |
| State Mgmt    | Kotlin Flow, ViewModel   |
| Billing       | Google Play Billing v8+  |
| DI            | Koin                     |
| Architecture  | MVVM + UseCases          |
| Language      | Kotlin (Coroutines)      |

---

## 📦 Use Cases

Each billing operation is encapsulated in a dedicated UseCase class, ensuring **single responsibility** and **testability**.

| UseCase Name | Responsibility |
|---------------|----------------|
| `StartBillingConnectionUseCase` | Initializes the billing client |
| `TerminateConnectionUseCase` | Ends the billing connection safely |
| `QueryProductDetailsUseCase` | Fetches product details (INAPP / SUBS) |
| `PurchaseProductUseCase` | Initiates the purchase flow |
| `QueryActivePurchasesUseCase` | Checks if user owns a product/subscription |
| `ConsumePurchaseUseCase` | Consumes one-time (INAPP) purchases |
| `AcknowledgePurchaseUseCase` | Acknowledges subscriptions or purchases |

---

## 🔧 Setup

### 1️⃣ Add Billing Permission

In your `AndroidManifest.xml`:
```xml
<uses-permission android:name="com.android.vending.BILLING" />
🧱 Repository Layer

Implements both INAPP (one-time purchases) and SUBS (subscriptions) in a unified billing repository:

✅ Start / End connection
✅ Query products
✅ Handle purchases & acknowledgment
✅ Query active items
✅ Consume purchases

All operations use coroutine Flows for reactive updates and safe lifecycle handling.
👨‍💻 Contributing

Contributions, issues, and pull requests are welcome!
If you find this repository helpful, ⭐ it and share it with the Android community 💙
MIT License

Copyright (c) 2025

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the “Software”), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND.
