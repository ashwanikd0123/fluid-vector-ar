# Project Milestones

### **Milestone 1: Core Canvas & Pointer Engine (Day 1 - Day 5)**

> **Goal:** High-performance Canvas rendering, Bezier curve smoothing, aur Non-Stylus (Finger Offset & Zoom) features ready karna.

* **Day 1: Project & Architecture Setup**
* Multi-module project creation (`:core:canvas`, `:core:database`, `:core:camera`, `:feature:editor`).
* Dependencies setup: Jetpack Compose, Hilt, Coroutines, Room, CameraX.


* **Day 2: Basic Compose Canvas & Matrix Handling**
* 2-Finger Pan, Pinch-to-Zoom, aur Rotation Matrix transformation handling.
* Reset canvas position gesture setup.


* **Day 3: Bezier Path & Brush Engine**
* Raw touch points ko Smooth Bezier Curves (`Path.cubicTo`) me convert karna.
* Stroke Width, Color Picker (HSV), aur Brush Types (Pencil, Highlighter, Neon Glow).


* **Day 4: Ergonomic Pointer System (The Finger-Drawing Solution)**
* Virtual Reticle (+30dp Offset Pointer Mode) implementation.
* Magnifying Glass Lens (2x Zoom Bubble over touch location).


* **Day 5: Milestone 1 Testing & Refinement**
* Canvas gesture conflict resolution (Pan vs Draw gesture priority).
* 60 FPS performance verification on low/mid-range device.



---

### **Milestone 2: Multi-Layer & Local Persistence (Day 6 - Day 10)**

> **Goal:** Layer management, Custom Shape Smoothing, aur Room Database with Vector Serialization.

* **Day 6: Layer Management System**
* Multi-layer stack UI (Up to 10 layers: Add, Delete, Visibility Toggle, Lock Layer).
* Layer Opacity & Blending Modes (*Normal, Multiply, Screen*).


* **Day 7: Shape Auto-Smoothing Engine**
* Post-stroke geometric shape recognition (Rough circle/line/rectangle to perfect vector primitives).


* **Day 8: Room DB Setup & Entity Design**
* `ProjectEntity` and `LayerEntity` schemas creation.
* Hilt Database Module configuration.


* **Day 9: Binary/JSON Vector Serialization**
* Custom Path-to-JSON serializer/deserializer to save artwork state to local storage without quality loss.
* Auto-save mechanism on editor exit.


* **Day 10: Gallery & Project Management UI**
* Dashboard/Home Screen to display saved projects with generated thumbnails.
* Open, Rename, Duplicate, Delete project operations.



---

### **Milestone 3: AR Camera Tracing & Vision Integration (Day 11 - Day 15)**

> **Goal:** CameraX live overlay tracing, Photo-to-Stencil converter, aur ML Kit QR Scanner.

* **Day 11: CameraX Pipeline Integration**
* Live 60 FPS CameraX Preview Layer placed directly under the Compose Canvas.
* Flashlight toggle and Tap-to-Focus metering controls.


* **Day 12: AR Tracing & Blend Controller**
* Real-time Opacity slider for blending vector artwork over the live camera feed.


* **Day 13: Photo-to-Stencil Filter Engine**
* Capture real-world photo -> Apply Edge Detection filter (`ColorMatrix` / `RenderEffect`) -> Instant traceable stencil overlay.


* **Day 14: ML Kit Barcode / QR Scanner Module**
* Embed ML Kit Barcode API to scan project sharing QR codes directly via Camera.


* **Day 15: Milestone 3 Integration & Test**
* Camera permission edge-cases handling.
* Smooth transition test between Normal Canvas Mode and AR Tracing Mode.



---

### **Milestone 4: Auth, Export, Polish & Play Store Release (Day 16 - Day 20)**

> **Goal:** Google Sign-In, Export formats (PNG/SVG), App Optimization, and Play Store Publishing.

* **Day 16: Authentication & Security**
* Google OAuth2 / Firebase Auth integration.
* `EncryptedSharedPreferences` for secure token storage.


* **Day 17: Export Engine**
* Vector artwork render and export to PNG (Transparent), JPEG, and native **SVG** file format.
* Native System Share Sheet integration.


* **Day 18: Performance & Memory Optimization**
* Canvas bitmap recycling, ProGuard / R8 rules setup, Dynamic memory leak checks via LeakCanary.
* Dynamic vector preview optimization.


* **Day 19: Internal Testing Track Setup**
* Generate Signed Release App Bundle (`.aab`).
* Setup Play Console App Listing (Graphics, Screenshots, Privacy Policy).
* Upload to Closed Testing Track (20 Testers compliance).


* **Day 20: Final Polish & Portfolio Case Study**
* Final Play Store Submission.
* GitHub README setup with GIFs, Tech Specs, Architecture Diagram for Freelance Portfolio.



---

### **Daily Target Tracker Summary**

| Phase | Days | Focus Area | Deliverable |
| --- | --- | --- | --- |
| **Milestone 1** | Days 1–5 | Core Canvas, Bezier, Reticle & Zoom Lens | Functional Smooth Canvas with Reticle |
| **Milestone 2** | Days 6–10 | Layers, Auto-Shape, Room DB & Vector Save | Offline-first Project Editor with Layers |
| **Milestone 3** | Days 11–15 | CameraX, AR Tracing & Stencil Filter | Live AR Camera Overlay & QR Scanner |
| **Milestone 4** | Days 16–20 | Auth, SVG Export, Play Store & Portfolio Setup | Published Play Store App + GitHub Case Study |
