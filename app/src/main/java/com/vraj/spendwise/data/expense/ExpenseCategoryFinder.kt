package com.vraj.spendwise.data.expense

import com.vraj.spendwise.R

object ExpenseCategoryFinder {

    fun getCategory(text: String): Category = with(text.lowercase().trim()) {
        when {
            isBlank() -> Category.OTHER
            CategoryData.food.any { contains(it) } -> Category.FOOD
            CategoryData.transport.any { contains(it) } -> Category.TRANSPORT
            CategoryData.shopping.any { contains(it) } -> Category.SHOPPING
            CategoryData.entertainment.any { contains(it) } -> Category.ENTERTAINMENT
            CategoryData.utilities.any { contains(it) } -> Category.UTILITIES
            CategoryData.healthcare.any { contains(it) } -> Category.HEALTHCARE
            CategoryData.subscription.any { contains(it) } -> Category.SUBSCRIPTION
            CategoryData.education.any { contains(it) } -> Category.EDUCATION
            else -> Category.OTHER
        }
    }

    enum class Category {
        FOOD,
        TRANSPORT,
        SHOPPING,
        ENTERTAINMENT,
        UTILITIES,
        HEALTHCARE,
        SUBSCRIPTION,
        EDUCATION,
        OTHER;

        fun getName(): String = when (this) {
            FOOD -> "Food"
            TRANSPORT -> "Transport"
            SHOPPING -> "Shopping"
            ENTERTAINMENT -> "Entertainment"
            UTILITIES -> "Utilities"
            HEALTHCARE -> "Healthcare"
            SUBSCRIPTION -> "Subscription"
            EDUCATION -> "Education"
            OTHER -> "Other"
        }

        fun getIcon(): Int = when (this) {
            FOOD -> R.drawable.ic_food
            TRANSPORT -> R.drawable.ic_transport
            SHOPPING -> R.drawable.ic_shopping
            ENTERTAINMENT -> R.drawable.ic_entertainment
            UTILITIES -> R.drawable.ic_utilities
            HEALTHCARE -> R.drawable.ic_healthcare
            SUBSCRIPTION -> R.drawable.ic_subscription
            EDUCATION -> R.drawable.ic_education
            OTHER -> R.drawable.ic_other
        }
    }
}

private object CategoryData {

    val food = listOf(
        "restaurant", "cafe", "coffee", "tea", "juice", "smoothie", "bakery", "cake", "pastry",
        "bread", "sandwich", "pizza", "burger", "fries", "salad", "noodles", "pasta", "sushi",
        "ramen", "steak", "seafood", "shrimp", "lobster", "chicken", "wings", "barbecue", "bbq",
        "buffet", "breakfast", "brunch", "lunch", "dinner", "snack", "dessert", "ice cream",
        "chocolate", "candy", "donut", "cookie", "taco", "burrito", "wrap", "shawarma", "kebab",
        "curry", "rice", "biryani", "dal", "chapati", "roti", "naan", "paneer", "dumpling", "dim sum",
        "hot pot", "falafel", "hummus", "soup", "stew", "grill", "fried", "roast", "takeout",
        "delivery", "fast food", "drive thru", "meal kit", "baking", "supermarket", "organic",
        "produce", "fruit", "vegetable", "meat", "fish", "egg", "deli", "butcher", "cheese", "milk",
        "yogurt", "butter", "oil", "flour", "spices", "herbs", "condiments", "sauce", "vinegar",
        "salt", "sugar", "snacks", "chips", "popcorn", "nuts", "cereal", "instant", "frozen",
        "ready meal", "sports drink", "beverage", "alcohol", "wine", "beer", "cocktail", "mocktail",
        "energy drink"
    )

    val transport = listOf(
        "uber", "uber trip", "uber ride", "lyft", "ola", "cab", "taxi", "auto", "rickshaw", "bus",
        "bus fare", "bus ticket", "local bus", "intercity bus", "greyhound", "train", "train fare",
        "rail ticket", "subway", "metro", "local transit", "commute", "ride", "daily commute",
        "carpool", "shared ride", "bike rental", "bike ride", "e-scooter", "scooter ride", "fuel",
        "gas", "petrol", "diesel", "toll", "toll tax", "parking", "valet", "car rental",
        "vehicle rental", "uber eats delivery", "transport service", "airport shuttle", "car",
        "airport taxi", "fare", "fares", "transportation", "public transport", "local travel",
        "suburban train", "express train", "auto fare", "bus pass", "monthly pass", "weekly pass",
        "transit pass", "commuter rail", "taxi ride", "uber pool", "fuel top-up", "shell", "esso",
        "petro-canada", "gas station", "onroute", "refueling", "truck rental", "moving truck",
        "bike share", "zipcar", "getaround", "turo", "blablacar", "scooty", "cycle", "bicycle",
        "motorbike", "bike fuel", "parking ticket", "parking pass", "car wash", "wheel alignment",
        "vehicle repair", "auto service", "transit system", "road trip", "highway toll",
        "fuel surcharge", "transport card", "presto card", "opus card", "myki card", "octopus card",
        "smart rider", "contactless fare", "local shuttle", "ride fare", "metro fare", "city bus"
    )

    val shopping = listOf(
        "accessories", "aliexpress", "aliexpress mall", "amazon", "amazon center", "apparel",
        "apparel purchase", "bags", "bestbuy", "bestbuy online", "boutique", "boutique online",
        "clothing", "clothing outlet", "costco", "costco online", "decor", "decor outlet",
        "decathlon", "decathlon store", "electronics", "electronics outlet", "ebay", "ebay purchase",
        "fashion", "fashion store", "furniture", "furniture purchase", "gadgets", "gadgets online",
        "gift shop", "gift shop outlet", "hm", "hm online", "home goods", "home goods center",
        "lenskart", "lenskart mall", "mall", "mall shopping", "myntra", "myntra order", "order",
        "online", "online order", "outlet", "purchase", "retail", "retail center", "retail store",
        "shein", "shein shop", "shoes", "shoes store", "store", "store outlet", "target",
        "target mall", "uniqlo", "uniqlo order", "walmart", "walmart center", "super store",
        "zara", "zara store", "apparel shop", "fashion outlet", "electronics store", "furniture mall",
        "decor store", "accessory shop", "bags outlet", "gift center", "mobile store", "grocery",
        "laptop purchase", "headphones", "smartwatch", "tech gadget", "smartphone", "tshirt",
        "hoodie", "jacket", "footwear", "heels", "boots", "sneakers", "home decor", "table", "sofa",
        "bed", "bookshelf", "mirror", "lamp", "shopping", "shopping spree", "window shopping",
        "bargain store", "thrift", "thrift shop", "dollar store", "deals", "offers", "wishlist",
        "cart", "jeans"
    )

    val entertainment = listOf(
        "amusement", "amusement charge", "amusement service", "arcade", "arcade plan",
        "arcade service", "cinema", "cinema charge", "comedy", "comedy fee", "concert",
        "concert charge", "disney", "disney subscription", "event", "event charge", "festival",
        "festival fee", "fun", "fun fee", "game", "game charge", "gaming", "gaming plan",
        "live event", "live show", "movie", "movie plan", "music", "music charge", "netflix",
        "netflix subscription", "nintendo", "nintendo service", "opera", "opera ticket",
        "playstation", "playstation charge", "recreation", "recreation fee", "show", "show plan",
        "spotify", "spotify subscription", "standup", "standup comedy", "steam", "steam charge",
        "subscription plan", "theater", "theater charge", "video game", "video game purchase", "xbox",
        "xbox subscription", "youtube", "youtube premium", "binge", "cinema ticket", "movie ticket",
        "music concert", "musical", "gala", "live music", "club entry", "dj night", "online game",
        "pc game", "ps5", "switch", "stadia", "apple music", "amazon prime", "hotstar", "zee5", "voot",
        "sonyliv", "audible", "podcast premium", "streaming", "media app", "multimedia",
        "entertainment charge", "watch party", "reel", "shorts", "film", "web series", "epic show",
        "season", "episode", "content", "paid content", "exclusive show", "fun center",
        "kids entertainment", "family outing", "marvel", "anime", "crunchyroll", "vr game"
    )

    val utilities = listOf(
        "ac", "ac bill", "ac subscription", "bill", "bill charge", "bill plan", "bill subscription",
        "bills", "bills fee", "bills subscription", "cable", "cable bill", "cable plan",
        "cable service", "data", "data bill", "data recharge", "electricity", "electricity charge",
        "electricity fee", "gas", "gas bill", "gas service", "heating", "heating charge",
        "heating service", "hoa", "hoa fee", "home utility", "hydro", "hydro bill", "hydro plan",
        "insurance", "insurance fee", "internet", "internet charge", "internet plan", "mobile",
        "mobile bill", "mobile recharge", "mortgage", "mortgage payment", "phone", "phone bill",
        "phone plan", "postpaid", "postpaid bill", "postpaid plan", "prepaid", "prepaid bill",
        "prepaid plan", "property tax", "property tax payment", "recharge", "recharge card", "rent",
        "rent bill", "rent fee", "service", "service charge", "topup", "topup plan", "utility",
        "utility bill", "utility charge", "utility fee", "vodafone", "rogers", "bell", "telus",
        "fido", "freedom mobile", "koodo", "virgin plus", "shaw", "primus", "xplornet", "cogeco",
        "electric bill", "heating bill", "natural gas", "boiler", "wifi", "wifi service", "wifi plan",
        "wifi bill", "data topup", "monthly plan", "tv bill", "tv package", "monthly recharge",
        "daily data", "streaming plan", "monthly subscription", "charger", "bottle"
    )

    val healthcare = listOf(
        "doctor", "doctor appointment", "dentist", "dental clinic", "hospital", "hospital bill",
        "clinic", "medical clinic", "pharmacy", "pharmacy purchase", "chemist", "medicine", "meds",
        "prescription", "prescription refill", "checkup", "health checkup", "diagnostic",
        "diagnostic center", "lab test", "blood test", "x-ray", "scan", "mri", "ct scan",
        "ultrasound", "therapy", "therapy session", "physical therapy", "physio", "physiotherapy",
        "vaccination", "vaccination fee", "immunization", "treatment", "medical treatment", "surgery",
        "surgery cost", "eye test", "optical", "optician", "eyewear purchase", "contact lens",
        "glasses", "wellness", "wellness center", "clinic visit", "first aid", "emergency room", "er",
        "ambulance", "ambulance charge", "insurance claim", "health insurance", "mental health",
        "psychologist", "counseling", "counselor", "therapy session", "chiropractor", "acupuncture",
        "ayurveda", "alternative medicine", "herbal medicine", "homeopathy", "mask", "sanitizer",
        "healthcare service", "medical service", "consultation", "urgent care", "walk-in clinic",
        "dental care", "orthodontist", "dermatologist", "skin clinic", "cardiology", "cardiologist",
        "psychiatrist", "MRI scan", "CT scan", "EKG", "ecg", "ultrasound scan", "pet scan",
        "pediatrician", "pediatrics", "vaccination center", "vaccine", "booster", "annual physical",
        "health screening", "nutritionist", "dietician", "weight loss clinic", "fitness test",
        "medical supplies", "bandages", "first aid kit", "health check", "covid test",
        "covid testing", "blood pressure", "bp check", "cholesterol test", "health check",
        "eye clinic", "hearing test", "hearing aid", "speech therapy", "occupational therapy"
    )

    val subscription = listOf(
        "subscription", "subscribe", "membership", "plan", "package", "bundle", "tier", "basic plan",
        "premium plan", "pro plan", "monthly fee", "annual fee", "yearly fee", "billing cycle",
        "recurring", "auto-renew", "renewal", "renew", "trial", "free trial", "digital service",
        "digital membership", "digital subscription", "music subscription", "spotify", "apple music",
        "amazon music", "tidal", "deezer", "youtube music", "google play music", "pandora",
        "soundcloud go", "video subscription", "netflix", "hulu", "prime video", "disney+", "hotstar",
        "hbo max", "peacock", "paramount+", "apple tv+", "crunchyroll", "funimation", "vrv",
        "plex pass", "kanopy", "mubi", "cloud subscription", "icloud", "google drive", "dropbox",
        "onedrive", "mega", "box cloud", "pcloud", "software subscription", "microsoft 365",
        "office 365", "adobe creative cloud", "canva pro", "figma pro", "notion plus",
        "evernote premium", "slack pro", "zoom pro", "trello premium", "asana premium",
        "jira cloud", "confluence cloud", "security subscription", "vpn", "nordvpn", "expressvpn",
        "surfshark", "cyberghost vpn", "private internet access", "protonvpn",
        "antivirus subscription", "mcafee", "norton 360", "bitdefender", "avast premium", "kaspersky",
        "malwarebytes premium", "gaming subscription", "xbox game pass", "playstation plus",
        "nintendo switch online", "epic game store plus", "stadia pro", "geforce now", "ea play",
        "ubisoft+", "riot pass", "blizzard pass", "reading subscription", "kindle unlimited",
        "audible", "scribd", "wattpad premium", "blinkist", "medium membership", "new york times",
        "washington post", "the economist", "magazine subscription", "newspaper subscription",
        "fitness subscription", "peloton", "fitbit premium", "myfitnesspal premium", "calm premium",
        "headspace plus", "fiton pro", "education subscription", "udemy pro", "coursera plus",
        "edx verified", "linkedin learning", "skillshare premium", "pluralsight", "datacamp",
        "codeacademy pro", "brilliant premium", "masterclass"
    )

    val education = listOf(
        "school", "college", "university", "academy", "institute", "campus", "class", "course",
        "lesson", "training", "workshop", "seminar", "conference", "bootcamp", "tutor", "mentorship",
        "teacher", "professor", "lecturer", "instructor", "student", "pupil", "enrollment",
        "registration", "admission", "exam", "test", "quiz", "assessment", "assignment", "project",
        "homework", "degree", "diploma", "certificate", "certification", "major", "minor",
        "curriculum", "syllabus", "textbook", "workbook", "notebook", "stationery", "pen", "pencil",
        "eraser", "marker", "highlighter", "calculator", "lab", "laboratory", "experiment", "research",
        "book", "library", "ebook", "journal", "thesis", "dissertation", "paper", "publication",
        "presentation", "slide", "whiteboard", "chalkboard", "blackboard", "field trip", "study tour",
        "scholarship", "grant", "internship", "fellowship", "distance learning", "online class",
        "virtual learning", "e-learning", "MOOC", "lecture hall", "study group", "academic", "faculty",
        "department", "dean", "principal", "headmaster", "tuition", "fees", "school bus",
        "student card", "ID card", "graduation", "cap and gown", "alumni", "education fair",
        "exchange program", "lab coat", "microscope", "science kit", "art class", "music class",
        "language course", "coding bootcamp", "math club", "robotics club", "debate team"
    )
}