package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        AnimalListing::class,
        ReelVideo::class,
        ReelComment::class,
        AppNotification::class,
        ChatMessage::class,
        UserProfile::class,
        CategoryEntity::class,
        BuyerInquiry::class,
        SellerReview::class,
        SearchHistoryEntity::class,
        BookingEntity::class,
        AppSettingEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class MaveshiDatabase : RoomDatabase() {
    abstract fun animalDao(): AnimalDao
    abstract fun reelDao(): ReelDao
    abstract fun notificationDao(): NotificationDao
    abstract fun chatDao(): ChatDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun categoryDao(): CategoryDao
    abstract fun inquiryDao(): InquiryDao
    abstract fun sellerReviewDao(): SellerReviewDao
    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun bookingDao(): BookingDao
    abstract fun appSettingDao(): AppSettingDao

    companion object {
        @Volatile
        private var INSTANCE: MaveshiDatabase? = null

        fun getInstance(context: Context): MaveshiDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MaveshiDatabase::class.java,
                    "maveshi_mandi.db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.let { seedDatabase(it) }
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun seedDatabase(database: MaveshiDatabase) {
            val animalDao = database.animalDao()
            if (animalDao.getCount() == 0) {
                val seedAnimals = listOf(
                    AnimalListing(
                        title = "Makhi Cheeni Goat with 2 Kids",
                        category = "Goat",
                        breed = "Makhi Cheeni",
                        price = 150000,
                        city = "Lahore",
                        description = "Makhi Cheeni Goat with 2 Kids For Sale.\n\nBreed: Makhi Cheeni\nVery sweet tempered, exceptional milk quality, clean teeth (2 danda), both kids are active and healthy. Inspection welcome in Lahore.",
                        imageResName = "goat_cheeni",
                        isFeatured = true,
                        isNew = true,
                        viewsCount = 47,
                        postedTimeText = "14 hours ago",
                        sellerName = "Zeeshan Zamurd",
                        sellerPhone = "03038692236",
                        isUserListing = false,
                        isSold = false,
                        ageText = "2 Years",
                        teethCount = "2 Danda",
                        weightKg = "52 kg",
                        milkCapacity = "3.5 Litres/day",
                        isVaccinated = true
                    ),
                    AnimalListing(
                        title = "Pure Sahiwal Dairy Cow with Calf",
                        category = "Cow",
                        breed = "Sahiwal Dairy",
                        price = 320000,
                        city = "Lahore",
                        description = "Pure Sahiwal champion red cow with newborn heifer calf. Top lineage dairy champion with 16+ Litres guaranteed daily milk record. Home grass raised, calm temperament.",
                        imageResName = "sahiwal_cow",
                        isFeatured = true,
                        isNew = true,
                        viewsCount = 112,
                        postedTimeText = "1 day ago",
                        sellerName = "Zeeshan Zamurd",
                        sellerPhone = "03038692236",
                        isUserListing = true,
                        isSold = false,
                        ageText = "3.5 Years",
                        teethCount = "4 Danda",
                        weightKg = "380 kg",
                        milkCapacity = "16 Litres/day",
                        isVaccinated = true
                    ),
                    AnimalListing(
                        title = "Nili Ravi Kundhi Buffalo",
                        category = "Buffalo",
                        breed = "Nili Ravi Kundhi",
                        price = 420000,
                        city = "Nankana Sahib",
                        description = "Jet black pure Nili Ravi dairy buffalo, second calving with healthy calf. High fat thick milk production (18L daily). Very docile nature.",
                        imageResName = "nili_buffalo",
                        isFeatured = true,
                        isNew = true,
                        viewsCount = 94,
                        postedTimeText = "19m ago",
                        sellerName = "Zeeshan Zamurd",
                        sellerPhone = "03038692236",
                        isUserListing = true,
                        isSold = false,
                        ageText = "4 Years",
                        teethCount = "6 Danda",
                        weightKg = "520 kg",
                        milkCapacity = "18 Litres/day",
                        isVaccinated = true
                    ),
                    AnimalListing(
                        title = "Aseel Champion Cock",
                        category = "Chicken",
                        breed = "White Shamo / Aseel",
                        price = 3500,
                        city = "Lahore",
                        description = "Pure white champion bloodline Aseel cock. Tall standing height, aggressive stance, broad chest, fully healthy and vaccinated.",
                        imageResName = "chickens",
                        isFeatured = true,
                        isNew = false,
                        viewsCount = 68,
                        postedTimeText = "2 days ago",
                        sellerName = "Malik Faisal",
                        sellerPhone = "03129876543",
                        isUserListing = false,
                        isSold = false,
                        ageText = "1 Year",
                        teethCount = "N/A",
                        weightKg = "3.8 kg",
                        isVaccinated = true
                    ),
                    AnimalListing(
                        title = "Irani Kala Teeter Pair",
                        category = "Birds",
                        breed = "Irani Kala Teeter",
                        price = 18000,
                        city = "Multan",
                        description = "Active voice calling Irani Kala Teeter pair with cage. Clear, melodious whistle call, home domesticated and lively.",
                        imageResName = "irani_teeter",
                        isFeatured = false,
                        isNew = true,
                        viewsCount = 82,
                        postedTimeText = "3 hours ago",
                        sellerName = "Nouman Zamurd",
                        sellerPhone = "03009988776",
                        isUserListing = false,
                        isSold = false,
                        ageText = "8 Months",
                        teethCount = "N/A",
                        weightKg = "0.7 kg",
                        isVaccinated = true
                    ),
                    AnimalListing(
                        title = "Beetal Gulabi Goat Pair",
                        category = "Goat",
                        breed = "Beetal Gulabi",
                        price = 95000,
                        city = "Faisalabad",
                        description = "Young Beetal goat pair, white and pink skin, long pendulous ears. Perfect for Qurbani farming or home raising.",
                        imageResName = "goat_cheeni",
                        isFeatured = false,
                        isNew = true,
                        viewsCount = 38,
                        postedTimeText = "5 hours ago",
                        sellerName = "Chaudhry Farm",
                        sellerPhone = "03214445566",
                        isUserListing = false,
                        isSold = false,
                        ageText = "14 Months",
                        teethCount = "Kheera",
                        weightKg = "42 kg",
                        isVaccinated = true
                    ),
                    AnimalListing(
                        title = "Kajla Chhatra Ram",
                        category = "Lamb",
                        breed = "Sargodha Kajla",
                        price = 72000,
                        city = "Sargodha",
                        description = "Handsome heavy Kajla ram with signature black eye circles, dense wool fleece, sturdy bone structure. Raised on natural pasture grass.",
                        imageResName = "maveshi_splash",
                        isFeatured = true,
                        isNew = true,
                        viewsCount = 59,
                        postedTimeText = "1 day ago",
                        sellerName = "Malik Asghar",
                        sellerPhone = "03451122334",
                        isUserListing = false,
                        isSold = false,
                        ageText = "1.5 Years",
                        teethCount = "2 Danda",
                        weightKg = "58 kg",
                        isVaccinated = true
                    ),
                    AnimalListing(
                        title = "Cholistani Breeder Cow",
                        category = "Cow",
                        breed = "Cholistani Spotted",
                        price = 260000,
                        city = "Bahawalpur",
                        description = "Authentic Cholistani spotted red & white dairy cow. Hardy breed, high heat tolerance, gives 12L daily sweet creamy milk.",
                        imageResName = "sahiwal_cow",
                        isFeatured = false,
                        isNew = true,
                        viewsCount = 44,
                        postedTimeText = "2 days ago",
                        sellerName = "Rana Tariq",
                        sellerPhone = "03017778899",
                        isUserListing = false,
                        isSold = false,
                        ageText = "3 Years",
                        teethCount = "4 Danda",
                        weightKg = "340 kg",
                        milkCapacity = "12 Litres/day",
                        isVaccinated = true
                    ),
                    AnimalListing(
                        title = "Marecha Desert Camel",
                        category = "Camel",
                        breed = "Marecha Racing & Milk",
                        price = 480000,
                        city = "Rahim Yar Khan",
                        description = "Healthy young Marecha female camel, calm temperament, high milk production potential, tall and graceful posture.",
                        imageResName = "maveshi_splash",
                        isFeatured = false,
                        isNew = false,
                        viewsCount = 130,
                        postedTimeText = "3 days ago",
                        sellerName = "Sardar Khan",
                        sellerPhone = "03332211009",
                        isUserListing = false,
                        isSold = false,
                        ageText = "4 Years",
                        teethCount = "4 Danda",
                        weightKg = "490 kg",
                        milkCapacity = "8 Litres/day",
                        isVaccinated = true
                    ),
                    AnimalListing(
                        title = "Desi Farm Layers Batch",
                        category = "Chicken",
                        breed = "Desi Country Layers",
                        price = 850,
                        city = "Karachi",
                        description = "Desi egg laying hens ready for home farming. High egg yield, completely disease free and vaccinated.",
                        imageResName = "chickens",
                        isFeatured = false,
                        isNew = false,
                        viewsCount = 22,
                        postedTimeText = "4 days ago",
                        sellerName = "Siddiqui Livestock",
                        sellerPhone = "03335551234",
                        isUserListing = false,
                        isSold = false,
                        ageText = "8 Months",
                        teethCount = "N/A",
                        weightKg = "1.8 kg",
                        isVaccinated = true
                    ),
                    // Sold listings for User Profile ("SOLD (7)" history)
                    AnimalListing(
                        title = "Amritsari Beetal Bakra",
                        category = "Goat",
                        breed = "Amritsari Beetal",
                        price = 110000,
                        city = "Lahore",
                        description = "Sold to customer in Model Town.",
                        imageResName = "goat_cheeni",
                        isFeatured = false,
                        isNew = false,
                        viewsCount = 145,
                        postedTimeText = "Sold",
                        sellerName = "Zeeshan Zamurd",
                        sellerPhone = "03038692236",
                        isUserListing = true,
                        isSold = true
                    ),
                    AnimalListing(
                        title = "Red Sindhi Cow",
                        category = "Cow",
                        breed = "Red Sindhi",
                        price = 290000,
                        city = "Lahore",
                        description = "Sold successfully to dairy farm buyer.",
                        imageResName = "sahiwal_cow",
                        isFeatured = false,
                        isNew = false,
                        viewsCount = 98,
                        postedTimeText = "Sold",
                        sellerName = "Zeeshan Zamurd",
                        sellerPhone = "03038692236",
                        isUserListing = true,
                        isSold = true
                    ),
                    AnimalListing(
                        title = "Kamori Goat Breeder",
                        category = "Goat",
                        breed = "Sindhi Kamori",
                        price = 135000,
                        city = "Lahore",
                        description = "Sold to breeder in Faisalabad.",
                        imageResName = "goat_cheeni",
                        isFeatured = false,
                        isNew = false,
                        viewsCount = 210,
                        postedTimeText = "Sold",
                        sellerName = "Zeeshan Zamurd",
                        sellerPhone = "03038692236",
                        isUserListing = true,
                        isSold = true
                    )
                )
                animalDao.insertAll(seedAnimals)
            }

            // Seed User Profile
            val profileDao = database.userProfileDao()
            if (profileDao.getCount() == 0) {
                profileDao.insertOrUpdateProfile(
                    UserProfile(
                        id = 1,
                        fullName = "Zeeshan Zamurd",
                        handle = "@zeeshanzamurd",
                        phone = "03038692236",
                        email = "zeeshan.zamurd@gmail.com",
                        city = "Lahore, Punjab",
                        memberSince = "Member since March 2024",
                        isVerified = true,
                        rating = 4.9f,
                        reviewCount = 32,
                        followersCount = 412,
                        followingCount = 54,
                        bio = "Certified livestock breeder & dairy farmer in Lahore. Top quality Sahiwal cows, Makhi Cheeni goats, and Nili Ravi buffaloes.",
                        avatarResName = "goat_cheeni"
                    )
                )
            }

            // Seed Categories
            val categoryDao = database.categoryDao()
            if (categoryDao.getCount() == 0) {
                val seedCategories = listOf(
                    CategoryEntity("All", "All", "تمام جانور", "mall_maweshi_logo", 16, 0),
                    CategoryEntity("Cow", "Cow", "گائے / بچھڑے", "sahiwal_cow", 4, 1),
                    CategoryEntity("Goat", "Goat", "بکریاں / بکرے", "goat_cheeni", 5, 2),
                    CategoryEntity("Buffalo", "Buffalo", "بھینسیں / کٹے", "nili_buffalo", 2, 3),
                    CategoryEntity("Lamb", "Lamb", "چھترے / دنبے", "maveshi_splash", 2, 4),
                    CategoryEntity("Chicken", "Chicken", "مرغیاں / اصیل", "chickens", 2, 5),
                    CategoryEntity("Birds", "Birds", "پرندے / تیتر", "irani_teeter", 1, 6),
                    CategoryEntity("Camel", "Camel", "اونٹ / سانڈ", "maveshi_splash", 1, 7)
                )
                categoryDao.insertAll(seedCategories)
            }

            // Seed Buyer Inquiries / Offers
            val inquiryDao = database.inquiryDao()
            if (inquiryDao.getCount() == 0) {
                inquiryDao.insertInquiry(
                    BuyerInquiry(
                        listingId = 1,
                        listingTitle = "Makhi Cheeni Goat with 2 Kids",
                        buyerName = "Haji Rafiq Ahmad",
                        buyerPhone = "03215551122",
                        offerPrice = 140000,
                        message = "As-salamu alaykum. I can pick up today in Lahore if you agree on 140k.",
                        status = "PENDING"
                    )
                )
                inquiryDao.insertInquiry(
                    BuyerInquiry(
                        listingId = 2,
                        listingTitle = "Pure Sahiwal Dairy Cow",
                        buyerName = "Chaudhry Bilal",
                        buyerPhone = "03004443322",
                        offerPrice = 300000,
                        message = "Interested in dairy cow for farm in Kasur. Cash ready upon physical inspection.",
                        status = "ACCEPTED"
                    )
                )
            }

            // Seed Seller Reviews
            val reviewDao = database.sellerReviewDao()
            if (reviewDao.getCount() == 0) {
                reviewDao.insertReview(
                    SellerReview(
                        sellerPhone = "03038692236",
                        reviewerName = "Muhammad Rizwan",
                        rating = 5,
                        comment = "MashAllah pure Makhi Cheeni goat delivered safely. Clean teeth and exact milk yield as claimed. Highly recommended trusted seller!",
                        timeAgo = "1 week ago"
                    )
                )
                reviewDao.insertReview(
                    SellerReview(
                        sellerPhone = "03038692236",
                        reviewerName = "Dr. Tariq Jamil",
                        rating = 5,
                        comment = "Bought Sahiwal cow last month. Excellent health condition and disease-free. Honest dealing.",
                        timeAgo = "3 weeks ago"
                    )
                )
            }

            // Seed Search History
            val searchDao = database.searchHistoryDao()
            searchDao.insertSearch(SearchHistoryEntity(query = "Makhi Cheeni"))
            searchDao.insertSearch(SearchHistoryEntity(query = "Sahiwal Cow"))
            searchDao.insertSearch(SearchHistoryEntity(query = "Nili Ravi Buffalo"))

            // Seed Bookings
            val bookingDao = database.bookingDao()
            if (bookingDao.getCount() == 0) {
                bookingDao.insertBooking(
                    BookingEntity(
                        listingId = 1,
                        listingTitle = "Makhi Cheeni Goat with 2 Kids",
                        buyerName = "Usman Ghani",
                        buyerPhone = "03117778899",
                        tokenAmount = 15000,
                        deliveryOption = "Self Pickup Lahore Mandi",
                        status = "CONFIRMED"
                    )
                )
            }

            // Seed Reels
            val reelDao = database.reelDao()
            if (reelDao.getCount() == 0) {
                val seedReels = listOf(
                    ReelVideo(
                        sellerName = "Nouman Zamurd",
                        sellerHandle = "@noumanzamurd",
                        caption = "#song #music #bollywood #newsong #love #irani #irani teeter #kalateterkidunya",
                        imageResName = "irani_teeter",
                        viewsCount = 3,
                        likesCount = 1,
                        commentsCount = 2,
                        isLiked = false,
                        isBookmarked = false,
                        isFollowing = false
                    ),
                    ReelVideo(
                        sellerName = "Zeeshan Zamurd",
                        sellerHandle = "@zeeshanzamurd",
                        caption = "SubhanAllah pure Makhi Cheeni goat walk in green field #mandi #bakra #goat #lahore",
                        imageResName = "goat_cheeni",
                        viewsCount = 248,
                        likesCount = 34,
                        commentsCount = 8,
                        isLiked = true,
                        isBookmarked = true,
                        isFollowing = true
                    ),
                    ReelVideo(
                        sellerName = "Sahiwal Cattle Farm",
                        sellerHandle = "@sahiwalbulls",
                        caption = "Heavy weight Sahiwal bull showcase for Qurbani 2026 #bull #sahiwal #maveshimandi",
                        imageResName = "sahiwal_cow",
                        viewsCount = 1042,
                        likesCount = 156,
                        commentsCount = 29,
                        isLiked = false,
                        isBookmarked = false,
                        isFollowing = false
                    )
                )
                reelDao.insertAll(seedReels)

                reelDao.insertComment(
                    ReelComment(
                        reelId = 1,
                        authorName = "Tariq Mahmood",
                        commentText = "Bohat khoob teeter subhanAllah! Price kya hai?",
                        timeAgo = "2h ago"
                    )
                )
                reelDao.insertComment(
                    ReelComment(
                        reelId = 1,
                        authorName = "Rana Usman",
                        commentText = "Kala teeter voice call check karna hai bhai.",
                        timeAgo = "1h ago"
                    )
                )
            }

            // Seed Notifications
            val notifDao = database.notificationDao()
            if (notifDao.getCount() == 0) {
                val seedNotifications = listOf(
                    AppNotification(
                        title = "New Offer Received!",
                        message = "Haji Rafiq offered Rs 140,000 for your Makhi Cheeni Goat. Tap to view inquiry.",
                        timeText = "10m",
                        section = "TODAY",
                        listingId = 1,
                        isRead = false
                    ),
                    AppNotification(
                        title = "New listing on Mall Maweshi",
                        message = "A Nili Ravi Buffalo listing just went up in Nankana Sahib. Open the app and have a look.",
                        timeText = "19m",
                        section = "TODAY",
                        listingId = 3,
                        isRead = false
                    ),
                    AppNotification(
                        title = "Price Drop Alert",
                        message = "A Goat listing in Lahore is now available at a discounted price.",
                        timeText = "15h",
                        section = "TODAY",
                        listingId = 6,
                        isRead = false
                    ),
                    AppNotification(
                        title = "Market Update",
                        message = "Qurbani 2026 pre-booking season is now open on Mall Maweshi.",
                        timeText = "1d",
                        section = "YESTERDAY",
                        listingId = 2,
                        isRead = false
                    )
                )
                notifDao.insertAll(seedNotifications)
            }

            // Seed App Settings for Developer & Platform Controls
            val settingDao = database.appSettingDao()
            if (settingDao.getCount() == 0) {
                val seedSettings = listOf(
                    AppSettingEntity(
                        key = "illegal_photo_filter_enabled",
                        value = "true",
                        description = "Automatically detect, flag, and blur illegal, abusive, or prohibited animal photos",
                        category = "MODERATION"
                    ),
                    AppSettingEntity(
                        key = "min_photos_required",
                        value = "3",
                        description = "Enforce at least 3 photos (Face, Body, Teeth/Danda) per livestock listing",
                        category = "GENERAL"
                    ),
                    AppSettingEntity(
                        key = "cloud_backend_endpoint",
                        value = "https://maveshi-backend.asia-east1.firebasedatabase.app",
                        description = "Complete backend database URL and Cloud Firestore synchronization bridge",
                        category = "BACKEND"
                    ),
                    AppSettingEntity(
                        key = "developer_email",
                        value = "bilalkhichi.156@gmail.com",
                        description = "Developer administrator email for notification dispatch and database monitoring",
                        category = "BACKEND"
                    ),
                    AppSettingEntity(
                        key = "social_share_watermark",
                        value = "true",
                        description = "Append official Mall Maweshi badge & verification link when shared on WhatsApp/Socials",
                        category = "SOCIAL"
                    ),
                    AppSettingEntity(
                        key = "allow_unverified_photos",
                        value = "false",
                        description = "Allow or quarantine unverified animal photos until reviewed by admin",
                        category = "MODERATION"
                    ),
                    AppSettingEntity(
                        key = "social_media_prefix",
                        value = "https://mallmaweshi.pk/listing/",
                        description = "Live canonical URL prefix for social media crawler previews and deep links",
                        category = "SOCIAL"
                    )
                )
                settingDao.insertAll(seedSettings)
            }
        }
    }
}
