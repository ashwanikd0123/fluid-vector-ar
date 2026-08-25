Monorepo aur multi-module se tension mat lo, Single-Module (`:app`) project ke andar clean packages/directories se structure karna best decision hai! Pehle app functional, robust aur publishable banana priorities par rakhte hain. Modularization baad me bhi ho sakti hai.

---

### **Single-Module Package Structure (Directory-Based)**

App ke `:app` module ke andar hi packages ko aise segregate karenge:

```text
com.app.fluidvectorar/
├── data/
│   ├── local/
│   │   ├── dao/                 # ProjectDao, LayerDao
│   │   ├── entity/              # ProjectEntity, LayerEntity
│   │   └── AppDatabase.kt
│   ├── repository/              # ProjectRepositoryImpl, AuthRepositoryImpl
│   └── serializer/              # VectorPathSerializer (JSON/Binary)
│
├── domain/                      # Clean Architecture (Optional/Lightweight)
│   ├── model/                   # CanvasPath, Layer, BrushStyle
│   └── repository/              # Repository Interfaces
│
├── ui/
│   ├── auth/                    # Login / Auth Screen & ViewModel
│   ├── home/                    # Project Gallery Screen, Cards & ViewModel
│   ├── editor/                  # Main Editor Screen, Tools & ViewModel
│   │   ├── components/          # BrushBar, Reticle, LayerPanel, OpacitySlider
│   │   └── canvas/              # Compose Canvas & Gesture Logic
│   ├── camera/                  # CameraX Preview & AR Overlay Logic
│   └── components/              # Global UI Components (Buttons, Dialogs)
│
├── utils/                       # Matrix Math, Edge Detection Filter, Reticle Helper
└── DI/                          # AppModule, DatabaseModule, Hilt Setup

```

---

### **Fluid Vector AR: Complete Screen Flow & UI Overview**

App me total **4 Primary Screens / Views** honge:

#### **1. Auth & Onboarding Screen (Splash / Login)**

* **Purpose:** Google Sign-In & Secure Auth Setup.
* **Key Components:**
* Clean Branding Logo & App Title (*Fluid Vector AR*).
* Single-Click "Continue with Google" Button.
* Quick Onboarding Carousel (3 Slides explaining: *Vector Canvas*, *Reticle Finger Precision*, *AR Tracing*).



#### **2. Home Screen (Project Gallery & Dashboard)**

* **Purpose:** User ke saare saved projects manage karna aur naya drawing canvas start karna.
* **Key Components:**
* **Top Bar:** User Profile Icon (Auth status), App Title, Search Bar.
* **Project Grid (2-Column Grid):** Saved projects with dynamic Vector/PNG Thumbnails, Project Name, and Last Modified Date.
* **Quick Actions:** Project Card Options (Rename, Duplicate, Export PNG/SVG, Delete).
* **FAB (Floating Action Button):** "New Canvas" button (Opens Project Setup Dialog: Name, Canvas Ratio).



#### **3. Main Editor Studio (The Primary Drawing Screen)**

* **Purpose:** Full-screen vector drawing canvas with finger-drawing reticle offset & zoom lens.
* **Key Components:**
* **Top Bar:** Back to Home, Project Title, Undo/Redo Buttons, Switch Mode (Normal Canvas vs AR Tracing), Export Button.
* **Central Canvas Area:** Full-screen Jetpack Compose Canvas (Supports Pan, Pinch-Zoom, Rotation).
* **Floating Virtual Reticle & Zoom Bubble:** Toggleable pointer for precise finger drawing.
* **Bottom Toolbar:**
* Tool Selector (Pencil, Marker, Glow Brush, Shape Smoother, Eraser).
* Color Picker Swatch & Brush Size/Opacity Sliders.
* Layer Manager Panel Toggle (Slide-up sheet for multi-layer ordering, opacity & visibility).





#### **4. AR Tracing & Camera Mode (Overlay View)**

* **Purpose:** Live camera preview ke sath image trace karna aur QR code scan karna.
* **Key Components:**
* **Live Camera Feed:** Real-time CameraX preview rendered behind the vector canvas.
* **AR Blend Slider:** Bottom overlay slider to control the opacity of the vector drawing over live camera preview.
* **Hardware Controls:** Flashlight Toggle, Tap-to-Focus meter, Flip Camera.
* **Photo-to-Stencil Button:** Snaps camera frame and applies instant Edge-Detection line art stencil.
* **QR Scanner Modal:** Integrated camera scanner to import projects via QR code.



---

### **User Navigation Flow Diagram**

$$\text{Splash / Auth} \longrightarrow \text{Home Gallery} \begin{cases} \longrightarrow \mathbf{\text{New Project Dialog}} \longrightarrow \mathbf{\text{Editor Studio}} \\ \longrightarrow \mathbf{\text{Open Existing Project}} \longrightarrow \mathbf{\text{Editor Studio}} \end{cases}$$

$$\text{Editor Studio} \rightleftharpoons \text{AR Tracing Overlay / Camera Mode}$$