# Software Requirement Specification (SRS)

## Project Name: Fluid Vector AR

---

### 1. Executive Summary

**Fluid Vector AR** is a high-performance Android application designed for digital artists, hobbyists, and everyday smartphone users. It bridges the gap between digital vector drawing and real-world tracing by combining a high-framerate Jetpack Compose Canvas engine with CameraX AR overlays. To solve the "fat-finger precision problem" inherent to non-stylus touchscreens, the app incorporates smart offset reticles, magnifying bubbles, and geometric stroke auto-smoothing.

---

### 2. Primary Goals & Target Audience

* **Target Audience:** Digital illustrators, casual sketching enthusiasts, and smartphone users drawing without a stylus.
* **Portfolio Showcase Value:** Demonstrates advanced custom graphics (Compose Canvas Math, Bezier interpolation), realtime camera hardware pipelines (CameraX, ML Kit), local persistence (Room DB, Binary Path Serialization), and security/auth patterns (OAuth2, Encrypted Storage).

---

### 3. Functional Requirements Specifications

#### **Module 1: High-Performance Vector Canvas Engine**

* **Matrix Transformations:**
* Support 2-finger Pan, Pinch-to-Zoom (10x to 5000% scale factor), and Free Canvas Rotation.
* Reset Canvas gesture (Double-tap to reset scale and transformation matrix to origin).


* **Bezier Stroke Interpolation:**
* Real-time path smoothing using Quadratic and Cubic Bezier curves (`Path.cubicTo`) calculated from touch motion events.
* Dynamic stroke-width evaluation based on velocity/speed of finger movement.


* **Brush Customization Engine:**
* **Controls:** HSV Color Picker with HEX input, Opacity/Alpha Slider (0–100%), Adjustable Stroke Width (1dp–100dp).
* **Brush Types:** Standard Solid Pencil, Textured Charcoal, Neon Glow (`BlurMaskFilter`), and Semi-transparent Highlighter/Marker.


* **Layer Management System:**
* Multi-layer canvas with support for up to 10 independent vector layers.
* Layer actions: Create, Delete, Reorder via Drag-and-Drop, Lock, Toggle Visibility.
* Layer opacity adjustments and Blending Modes (*Normal, Multiply, Screen, Overlay*).



#### **Module 2: Ergonomic Non-Stylus (Finger-Drawing) Enhancements**

* **Virtual Reticle (Offset Pointer Mode):**
* Displays a visible crosshair target offset by `+30dp` (Y-axis) from the active touch point, preventing finger occlusion.


* **Magnifier Glass Bubble (Precision Zoom Lens):**
* Hold-to-activate circular 2x magnification bubble rendered above the active touch coordinate.


* **Geometric Shape Auto-Smoothing & Snap:**
* Post-stroke geometric recognition engine that converts rough hand-drawn strokes into perfect primitives (Circle, Rectangle, Straight Line, Triangle).


* **Gesture Controls:**
* 2-Finger Tap: Undo | 3-Finger Tap: Redo.



#### **Module 3: AR Camera Tracing & Vision Integration**

* **Live AR Camera Pipeline:**
* CameraX surface rendered directly beneath/blended with the Canvas vector stack at 60 FPS.
* Hardware controls: Torch/Flashlight toggle and Tap-to-Focus metering.


* **Opacity & Blend Controller:**
* Real-time slider to control the transparency layer of the live camera feed relative to the drawing overlay.


* **Photo-to-Stencil Converter:**
* Capture real-world photos via CameraX and apply live edge-detection filters (`ColorMatrix` / `RenderEffect`) to instantly generate a stroke-traceable vector stencil.


* **QR Code Scanner:**
* Embedded ML Kit Barcode API to scan artwork share codes directly from the camera interface.



#### **Module 4: Persistence, Serialization & Export**

* **Room Local Database Architecture:**
* `ProjectEntity`: ID, Title, Canvas Dimensions, Thumbnail Path, Created At, Updated At.
* `LayerEntity`: Foreign key to Project, Index Order, Opacity, Blend Mode, Is Locked, Is Visible.


* **Binary Vector Serialization:**
* Save raw vector paths, coordinates, colors, and brush meta-data to disk as JSON/Binary data instead of flat images for instant project reconstruction.


* **Export Pipeline:**
* Render high-res output as PNG (Transparent background), JPEG, and native SVG (Scalable Vector Graphics).



#### **Module 5: Authentication, Security & Cloud Integration**

* **OAuth2 Authentication:**
* Single-click Google Sign-In and Firebase Auth integration.


* **Secure Storage:**
* Store auth tokens, encryption keys, and app preferences via `EncryptedSharedPreferences` / `MasterKey`.


* **Project Cloud Sync & Sharing:**
* Upload vector JSON bundles to cloud storage and generate encoded QR codes for cross-device canvas sharing.



---

### 4. Non-Functional Requirements & Performance Targets

* **UI Frame Rate:** Constant target of 60 FPS (120 FPS supported screens) during active path rendering and matrix operations.
* **Input Latency:** Touch-to-render path latency of less than 16ms.
* **Memory Management:** Zero memory leaks during layer additions; explicit dynamic bitmap recycling for layer thumbnails.
* **Architecture Pattern:** Clean Architecture + Modular MVVM/MVI (`:app`, `:core:ui-canvas`, `:core:camera`, `:core:database`, `:core:network`, `:feature:editor`, `:feature:auth`).

---

### 5. Tech Stack Summary

* **Language:** 100% Kotlin
* **UI Framework:** Jetpack Compose (Custom `Canvas`, `GraphicsLayer`, `Modifier.pointerInput`)
* **Hardware & ML:** CameraX API, Google ML Kit (Barcode Scanning)
* **Database & Auth:** Room Database, EncryptedSharedPreferences, Firebase Auth / Google Sign-In
* **Asynchronous Execution:** Kotlin Coroutines & Flow
* **Dependency Injection:** Hilt

---