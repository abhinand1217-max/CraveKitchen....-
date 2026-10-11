
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class CraveKitchen {

    private static final int PORT = 8080;
    private static final AtomicInteger ORDER_COUNTER = new AtomicInteger(1001);

    private record Food(
            int id, String name, String description,
            int price, String category, String image, String badge
    ) {}

    private static final Food[] MENU = {
        new Food(1, "Classic Burger", "Juicy patty, fresh lettuce and cheese.", 199,
                "Burgers", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=700", "Bestseller"),
        new Food(2, "Margherita Pizza", "Mozzarella, basil and rich tomato sauce.", 299,
                "Pizza", "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=700", "Popular"),
        new Food(3, "Crispy French Fries", "Golden, crispy fries with seasoning.", 99,
                "Sides", "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=700", ""),
        new Food(4, "Creamy Pasta", "Creamy sauce, herbs and parmesan.", 249,
                "Pasta", "https://images.unsplash.com/photo-1551183053-bf91a1d81141?w=700", "Chef's choice"),
        new Food(5, "Chicken Burger", "Grilled chicken with a signature sauce.", 229,
                "Burgers", "https://images.unsplash.com/photo-1606755962773-d324e0a13086?w=700", ""),
        new Food(6, "Pepperoni Pizza", "Classic pizza topped with pepperoni.", 349,
                "Pizza", "https://images.unsplash.com/photo-1628840042765-356cda07504e?w=700", "Popular"),
        new Food(7, "Chocolate Brownie", "Rich chocolate brownie for dessert.", 129,
                "Desserts", "https://images.unsplash.com/photo-1606313564200-e75d5e30476c?w=700", ""),
        new Food(8, "Fresh Lemonade", "Refreshing lemonade served chilled.", 79,
                "Drinks", "https://images.pexels.com/photos/96974/pexels-photo-96974.jpeg?auto=compress&cs=tinysrgb&w=700", ""),
        new Food(9, "Chocolate Milkshake", "A thick and creamy chocolate shake.", 149,
                "Drinks", "https://images.unsplash.com/photo-1572490122747-3968b75cc699?w=700", ""),
        new Food(10, "Garlic Bread", "Toasted bread with garlic and herbs.", 119,
                "Sides", "https://images.pexels.com/photos/1435907/pexels-photo-1435907.jpeg?auto=compress&cs=tinysrgb&w=700", ""),

        new Food(11, "Double Cheese Burger", "Two juicy patties with melted cheese.", 279,
                "Burgers", "https://images.pexels.com/photos/1639557/pexels-photo-1639557.jpeg?auto=compress&cs=tinysrgb&w=700", "Bestseller"),
        new Food(12, "Veggie Burger", "Crispy vegetable patty with fresh lettuce.", 179,
                "Burgers", "https://images.unsplash.com/photo-1520072959219-c595dc870360?w=700", "Vegetarian"),
        new Food(13, "BBQ Chicken Pizza", "Smoky BBQ chicken with melted mozzarella.", 379,
                "Pizza", "https://images.unsplash.com/photo-1513104890138-7c749659a591?w=700", "Popular"),
        new Food(14, "Garden Veggie Pizza", "Fresh vegetables, cheese and tomato sauce.", 289,
                "Pizza", "https://images.unsplash.com/photo-1571407970349-bc81e7e96d47?w=700", "Vegetarian"),
        new Food(15, "Alfredo Pasta", "Creamy white sauce pasta with herbs.", 269,
                "Pasta", "https://images.unsplash.com/photo-1551183053-bf91a1d81141?w=700", "Chef's choice"),
        new Food(16, "Spicy Chicken Pasta", "Pasta tossed in a spicy tomato sauce.", 289,
                "Pasta", "https://images.unsplash.com/photo-1693820206774-d4a769355142?auto=format&fit=crop&w=700&q=80", ""),
        new Food(17, "Crispy Onion Rings", "Golden onion rings with a crunchy coating.", 109,
                "Sides", "https://images.unsplash.com/photo-1639024471283-03518883512d?w=700", ""),
        new Food(18, "Chicken Nuggets", "Crispy bite-sized chicken pieces.", 169,
                "Sides", "https://images.unsplash.com/photo-1562967914-608f82629710?w=700", "Popular"),
        new Food(19, "New York Cheesecake", "Smooth and creamy classic cheesecake.", 189,
                "Desserts", "https://images.unsplash.com/photo-1533134242443-d4fd215305ad?w=700", "Bestseller"),
        new Food(20, "Chocolate Sundae", "Chocolate ice cream with rich chocolate sauce.", 159,
                "Desserts", "https://images.unsplash.com/photo-1563805042-7684c019e1cb?w=700", ""),
        new Food(21, "Iced Coffee", "Chilled coffee with a smooth creamy finish.", 119,
                "Drinks", "https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=700", "Popular"),
        new Food(22, "Mango Smoothie", "Refreshing mango smoothie made for summer.", 139,
                "Drinks", "https://images.unsplash.com/photo-1505252585461-04db1eb84625?w=700", ""),
        new Food(23, "Chicken Wrap", "Grilled chicken, fresh vegetables and sauce.", 199,
                "Sides", "https://images.unsplash.com/photo-1626700051175-6818013e1d4f?w=700", ""),
        new Food(24, "Pancake Stack", "Fluffy pancakes served with sweet toppings.", 179,
                "Desserts", "https://images.unsplash.com/photo-1528207776546-365bb710ee93?w=700", ""),
        new Food(25, "Fresh Orange Juice", "Refreshing orange juice served chilled.", 89,
                "Drinks", "https://images.unsplash.com/photo-1600271886742-f049cd451bba?w=700", "Fresh")
    };

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", CraveKitchen::handleRequest);
        server.setExecutor(null);
        server.start();

        System.out.println("==================================");
        System.out.println("       CRAVE KITCHEN ONLINE");
        System.out.println("==================================");
        System.out.println("Open: http://localhost:" + PORT);
        System.out.println("Press Ctrl+C to stop.");
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            if (path.equals("/") && method.equalsIgnoreCase("GET")) {
                sendHtml(exchange, 200, homePage());
            } else if (path.equals("/checkout") && method.equalsIgnoreCase("POST")) {
                handleCheckout(exchange);
            } else {
                sendHtml(exchange, 404,
                        messagePage("Page not found", "The requested page does not exist."));
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendHtml(exchange, 500,
                    messagePage("Something went wrong", "Please return to the menu and try again."));
        } finally {
            exchange.close();
        }
    }

    private static String homePage() {
        StringBuilder cards = new StringBuilder();

        for (Food food : MENU) {
            cards.append("""
                <article class="food-card" data-category="%s" data-search="%s">
                    <div class="food-image">
                        <img src="%s" alt="%s" loading="lazy" onerror="this.onerror=null;this.style.display='none';this.parentElement.insertAdjacentHTML('beforeend','<span class=&quot;image-fallback&quot;>🍽️</span>');">
                        %s
                    </div>
                    <div class="food-info">
                        <div class="food-title-row">
                            <h3>%s</h3>
                            <span class="price">₹%d</span>
                        </div>
                        <p>%s</p>
                        <button class="add-btn" type="button"
                            data-id="%d" data-name="%s" data-price="%d">
                            <span>+</span> Add to cart
                        </button>
                    </div>
                </article>
                """.formatted(
                    escapeHtml(food.category()),
                    escapeHtml((food.name() + " " + food.description()).toLowerCase()),
                    escapeHtml(food.image()),
                    escapeHtml(food.name()),
                    food.badge().isBlank() ? "" :
                            "<span class=\"badge\">" + escapeHtml(food.badge()) + "</span>",
                    escapeHtml(food.name()),
                    food.price(),
                    escapeHtml(food.description()),
                    food.id(),
                    escapeHtml(food.name()),
                    food.price()
            ));
        }

        String html = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Crave Kitchen | Food Delivered Fresh</title>
                <meta name="description" content="Discover delicious food at Crave Kitchen.">
                <style>
                    :root {
                        --orange: #f06428;
                        --dark: #202020;
                        --muted: #737373;
                        --cream: #fffaf5;
                    }

                    * { box-sizing: border-box; }
                    html { scroll-behavior: smooth; }
                    body {
                        margin: 0;
                        font-family: Arial, Helvetica, sans-serif;
                        background: #fff;
                        color: var(--dark);
                    }
                    button, input, textarea { font: inherit; }
                    button { cursor: pointer; }

                    .topbar {
                        background: #202020;
                        color: white;
                        padding: 10px 6%;
                        text-align: center;
                        font-size: 13px;
                    }
                    header {
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        gap: 20px;
                        padding: 20px 7%;
                        background: white;
                        border-bottom: 1px solid #eee;
                        position: sticky;
                        top: 0;
                        z-index: 10;
                    }
                    .logo {
                        font-size: 25px;
                        font-weight: 900;
                        letter-spacing: -1px;
                    }
                    .logo span { color: var(--orange); }
                    nav {
                        display: flex;
                        align-items: center;
                        gap: 25px;
                    }
                    nav a {
                        text-decoration: none;
                        color: #333;
                        font-size: 14px;
                        font-weight: 600;
                    }
                    .cart-link {
                        background: var(--orange);
                        color: white;
                        border: none;
                        padding: 12px 18px;
                        border-radius: 10px;
                        font-weight: 800;
                    }

                    .hero {
                        margin: 28px auto;
                        width: 90%;
                        min-height: 360px;
                        border-radius: 24px;
                        overflow: hidden;
                        display: flex;
                        align-items: center;
                        background:
                            linear-gradient(90deg, rgba(20,20,20,.88), rgba(20,20,20,.35)),
                            url("https://images.unsplash.com/photo-1547592180-85f173990554?w=1600") center/cover;
                    }
                    .hero-content {
                        max-width: 610px;
                        padding: 55px;
                        color: white;
                    }
                    .eyebrow {
                        color: #ffc7a8;
                        font-size: 13px;
                        font-weight: 800;
                        letter-spacing: 2px;
                        text-transform: uppercase;
                    }
                    .hero h1 {
                        font-size: clamp(36px, 5vw, 62px);
                        line-height: 1.05;
                        letter-spacing: -2px;
                        margin: 15px 0;
                    }
                    .hero p {
                        color: #f0f0f0;
                        line-height: 1.7;
                        max-width: 430px;
                    }
                    .primary-btn {
                        display: inline-block;
                        background: var(--orange);
                        color: white;
                        text-decoration: none;
                        border: none;
                        border-radius: 10px;
                        padding: 15px 22px;
                        margin-top: 15px;
                        font-weight: 800;
                    }

                    .benefits {
                        display: grid;
                        grid-template-columns: repeat(3, 1fr);
                        gap: 15px;
                        width: 86%;
                        max-width: 1100px;
                        margin: 32px auto 55px;
                    }
                    .benefit {
                        display: flex;
                        gap: 13px;
                        align-items: center;
                        padding: 20px;
                        background: var(--cream);
                        border-radius: 15px;
                    }
                    .benefit-icon { font-size: 28px; }
                    .benefit strong { display: block; margin-bottom: 5px; }
                    .benefit small { color: var(--muted); }

                    .section {
                        width: 86%;
                        max-width: 1250px;
                        margin: 0 auto 65px;
                    }
                    .section-heading {
                        display: flex;
                        justify-content: space-between;
                        align-items: end;
                        gap: 20px;
                        margin-bottom: 25px;
                    }
                    .section-heading h2 {
                        margin: 0 0 8px;
                        font-size: clamp(28px, 4vw, 38px);
                        letter-spacing: -1px;
                    }
                    .section-heading p { color: var(--muted); margin: 0; }
                    .search {
                        width: 260px;
                        max-width: 100%;
                        padding: 13px 15px;
                        border: 1px solid #e4e4e4;
                        border-radius: 10px;
                        outline-color: var(--orange);
                    }
                    .categories {
                        display: flex;
                        gap: 10px;
                        flex-wrap: wrap;
                        margin-bottom: 25px;
                    }
                    .category-btn {
                        background: white;
                        border: 1px solid #e5e5e5;
                        border-radius: 30px;
                        padding: 10px 18px;
                        color: #444;
                        font-weight: 700;
                    }
                    .category-btn.active {
                        background: var(--dark);
                        color: white;
                        border-color: var(--dark);
                    }

                    .food-grid {
                        display: grid;
                        grid-template-columns: repeat(3, minmax(0, 1fr));
                        gap: 23px;
                    }
                    .food-card {
                        border: 1px solid #eee;
                        border-radius: 17px;
                        overflow: hidden;
                        background: white;
                        transition: transform .2s, box-shadow .2s;
                    }
                    .food-card:hover {
                        transform: translateY(-4px);
                        box-shadow: 0 12px 30px #0000000c;
                    }
                    .food-image {
                        position: relative;
                        height: 210px;
                        background: #f4f4f4;
                    }
                    .image-fallback { display:flex; height:100%; align-items:center; justify-content:center; font-size:54px; }
                    .food-image img {
                        width: 100%;
                        height: 100%;
                        object-fit: cover;
                    }
                    .badge {
                        position: absolute;
                        left: 13px;
                        top: 13px;
                        background: white;
                        padding: 7px 10px;
                        border-radius: 20px;
                        font-size: 11px;
                        font-weight: 800;
                    }
                    .food-info { padding: 17px; }
                    .food-title-row {
                        display: flex;
                        justify-content: space-between;
                        align-items: start;
                        gap: 10px;
                    }
                    .food-title-row h3 { margin: 0; font-size: 18px; }
                    .price {
                        color: var(--orange);
                        font-weight: 900;
                        white-space: nowrap;
                    }
                    .food-info p {
                        color: var(--muted);
                        font-size: 13px;
                        line-height: 1.6;
                        min-height: 40px;
                    }
                    .add-btn {
                        width: 100%;
                        border: 1px solid var(--orange);
                        color: var(--orange);
                        background: white;
                        border-radius: 9px;
                        padding: 12px;
                        font-weight: 800;
                    }
                    .add-btn:hover {
                        background: var(--orange);
                        color: white;
                    }
                    .add-btn span { font-size: 18px; }
                    .no-results {
                        display: none;
                        padding: 30px;
                        text-align: center;
                        color: var(--muted);
                    }

                    .checkout-section {
                        background: var(--cream);
                        padding: 40px;
                        border-radius: 22px;
                    }
                    .checkout-layout {
                        display: grid;
                        grid-template-columns: 1fr 1fr;
                        gap: 35px;
                        align-items: start;
                    }
                    .checkout-section h2 { margin-top: 0; font-size: 30px; }
                    .muted { color: var(--muted); line-height: 1.6; }
                    .cart-list {
                        list-style: none;
                        padding: 0;
                        margin: 20px 0;
                    }
                    .cart-row {
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        gap: 10px;
                        padding: 13px 0;
                        border-bottom: 1px solid #e9e1da;
                    }
                    .cart-name { font-weight: 700; }
                    .cart-controls {
                        display: flex;
                        align-items: center;
                        gap: 9px;
                    }
                    .qty-btn {
                        border: 1px solid #ddd;
                        background: white;
                        border-radius: 6px;
                        width: 28px;
                        height: 28px;
                        font-weight: 900;
                    }
                    .cart-total {
                        display: flex;
                        justify-content: space-between;
                        font-size: 20px;
                        font-weight: 900;
                        padding: 15px 0;
                    }
                    .form-group { margin-bottom: 15px; }
                    .form-group label {
                        display: block;
                        font-size: 13px;
                        font-weight: 800;
                        margin-bottom: 7px;
                    }
                    .form-group input, .form-group textarea {
                        width: 100%;
                        border: 1px solid #ddd;
                        border-radius: 9px;
                        padding: 13px;
                        background: white;
                    }
                    .form-group textarea { min-height: 95px; resize: vertical; }
                    .submit-btn {
                        width: 100%;
                        padding: 15px;
                        border: 0;
                        border-radius: 10px;
                        background: var(--orange);
                        color: white;
                        font-weight: 900;
                    }
                    .submit-btn:disabled { opacity: .5; cursor: not-allowed; }
                    footer {
                        padding: 35px 7%;
                        background: #202020;
                        color: white;
                        display: flex;
                        justify-content: space-between;
                        flex-wrap: wrap;
                        gap: 15px;
                    }
                    footer p { color: #bbb; margin: 5px 0; }

                    @media (max-width: 900px) {
                        .food-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); }
                        .benefits, .section { width: 90%; }
                        .checkout-layout { grid-template-columns: 1fr; }
                    }
                    @media (max-width: 600px) {
                        header { padding: 16px 5%; }
                        nav { gap: 12px; }
                        nav a { display: none; }
                        .logo { font-size: 21px; }
                        .cart-link { padding: 10px 12px; }
                        .hero { width: 92%; min-height: 350px; }
                        .hero-content { padding: 28px; }
                        .benefits { grid-template-columns: 1fr; }
                        .food-grid { grid-template-columns: 1fr; }
                        .food-image { height: 230px; }
                        .section-heading { align-items: stretch; flex-direction: column; }
                        .search { width: 100%; }
                        .checkout-section { padding: 23px; }
                    }
                </style>
            </head>
            <body>
                <div class="topbar">Freshly prepared food, delivered to your door.</div>

                <header>
                    <div class="logo">CRAVE<span>KITCHEN.</span></div>
                    <nav>
                        <a href="#home">Home</a>
                        <a href="#menu">Our Menu</a>
                        <a href="#checkout">Checkout</a>
                        <button class="cart-link" id="cartJump">
                            🛒 Cart (<span id="cartCount">0</span>)
                        </button>
                    </nav>
                </header>

                <main>
                    <section class="hero" id="home">
                        <div class="hero-content">
                            <div class="eyebrow">Good food. Good mood.</div>
                            <h1>Your cravings,<br>our kitchen.</h1>
                            <p>
                                From irresistible burgers to cheesy pizzas,
                                discover your favourites made fresh to order.
                            </p>
                            <a class="primary-btn" href="#menu">Explore our menu →</a>
                        </div>
                    </section>

                    <section class="benefits">
                        <div class="benefit">
                            <span class="benefit-icon">🍳</span>
                            <div><strong>Freshly prepared</strong><small>Made when you order</small></div>
                        </div>
                        <div class="benefit">
                            <span class="benefit-icon">🚴</span>
                            <div><strong>Quick delivery</strong><small>Fresh food at your door</small></div>
                        </div>
                        <div class="benefit">
                            <span class="benefit-icon">❤️</span>
                            <div><strong>Made with care</strong><small>Food you'll love</small></div>
                        </div>
                    </section>

                    <section class="section" id="menu">
                        <div class="section-heading">
                            <div>
                                <h2>Explore our menu</h2>
                                <p>Find something delicious for every craving.</p>
                            </div>
                            <input id="searchInput" class="search" type="search"
                                placeholder="Search food..." aria-label="Search food">
                        </div>

                        <div class="categories" id="categories">
                            <button class="category-btn active" data-category="All">All items</button>
                            <button class="category-btn" data-category="Burgers">🍔 Burgers</button>
                            <button class="category-btn" data-category="Pizza">🍕 Pizza</button>
                            <button class="category-btn" data-category="Pasta">🍝 Pasta</button>
                            <button class="category-btn" data-category="Sides">🍟 Sides</button>
                            <button class="category-btn" data-category="Desserts">🍰 Desserts</button>
                            <button class="category-btn" data-category="Drinks">🥤 Drinks</button>
                        </div>

                        <div class="food-grid" id="foodGrid">
                            __CARDS__
                        </div>
                        <div class="no-results" id="noResults">
                            No food items found. Try another search.
                        </div>
                    </section>

                    <section class="section" id="checkout">
                        <div class="checkout-section">
                            <div class="checkout-layout">
                                <div>
                                    <h2>Your cart 🛒</h2>
                                    <p class="muted">Review your selection before placing your order.</p>
                                    <ul class="cart-list" id="cartItems">
                                        <li class="muted">Your cart is empty. Add something delicious!</li>
                                    </ul>
                                    <div class="cart-total">
                                        <span>Total</span><span id="cartTotal">₹0</span>
                                    </div>
                                    <p class="muted">
                                        Demo checkout only. No online payment is collected.
                                    </p>
                                </div>

                                <div>
                                    <h2>Delivery details</h2>
                                    <form id="checkoutForm" action="/checkout" method="POST">
                                        <input type="hidden" id="cartData" name="items" value="">

                                        <div class="form-group">
                                            <label for="customerName">Full name</label>
                                            <input id="customerName" name="customerName"
                                                type="text" maxlength="80"
                                                placeholder="Enter your name" required>
                                        </div>

                                        <div class="form-group">
                                            <label for="phone">Phone number</label>
                                            <input id="phone" name="phone" type="tel"
                                                maxlength="20" placeholder="Enter phone number" required>
                                        </div>

                                        <div class="form-group">
                                            <label for="address">Delivery address</label>
                                            <textarea id="address" name="address"
                                                maxlength="500" placeholder="House, street, city..."
                                                required></textarea>
                                        </div>

                                        <button class="submit-btn" id="placeOrder"
                                            type="submit" disabled>Place order · ₹0</button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </section>
                </main>

                <footer>
                    <div>
                        <div class="logo">CRAVE<span>KITCHEN.</span></div>
                        <p>Made for your appetite.</p>
                    </div>
                    <p>© 2026 Crave Kitchen. Demo website.</p>
                </footer>

                <script>
                    const cart = {};
                    const foodData = {};
                    let activeCategory = "All";

                    document.querySelectorAll(".add-btn").forEach(button => {
                        const id = Number(button.dataset.id);
                        foodData[id] = {
                            id: id,
                            name: button.dataset.name,
                            price: Number(button.dataset.price)
                        };

                        button.addEventListener("click", () => {
                            cart[id] = (cart[id] || 0) + 1;
                            renderCart();

                            const oldText = button.innerHTML;
                            button.innerHTML = "✓ Added";
                            setTimeout(() => button.innerHTML = oldText, 700);
                        });
                    });

                    function renderCart() {
                        const list = document.getElementById("cartItems");
                        list.replaceChildren();

                        const ids = Object.keys(cart).filter(id => cart[id] > 0);
                        let count = 0;
                        let total = 0;

                        if (ids.length === 0) {
                            const empty = document.createElement("li");
                            empty.className = "muted";
                            empty.textContent = "Your cart is empty. Add something delicious!";
                            list.appendChild(empty);
                        }

                        ids.forEach(id => {
                            const food = foodData[id];
                            const qty = cart[id];
                            count += qty;
                            total += food.price * qty;

                            const li = document.createElement("li");
                            li.className = "cart-row";

                            const details = document.createElement("div");
                            const name = document.createElement("div");
                            name.className = "cart-name";
                            name.textContent = food.name;

                            const subtotal = document.createElement("small");
                            subtotal.className = "muted";
                            subtotal.textContent = "₹" + (food.price * qty);
                            details.append(name, subtotal);

                            const controls = document.createElement("div");
                            controls.className = "cart-controls";

                            const minus = document.createElement("button");
                            minus.type = "button";
                            minus.className = "qty-btn";
                            minus.textContent = "−";
                            minus.setAttribute("aria-label", "Remove one " + food.name);
                            minus.addEventListener("click", () => {
                                cart[id]--;
                                if (cart[id] <= 0) delete cart[id];
                                renderCart();
                            });

                            const quantity = document.createElement("span");
                            quantity.textContent = qty;

                            const plus = document.createElement("button");
                            plus.type = "button";
                            plus.className = "qty-btn";
                            plus.textContent = "+";
                            plus.setAttribute("aria-label", "Add one " + food.name);
                            plus.addEventListener("click", () => {
                                cart[id]++;
                                renderCart();
                            });

                            controls.append(minus, quantity, plus);
                            li.append(details, controls);
                            list.appendChild(li);
                        });

                        document.getElementById("cartCount").textContent = count;
                        document.getElementById("cartTotal").textContent = "₹" + total;
                        document.getElementById("placeOrder").textContent = "Place order · ₹" + total;
                        document.getElementById("placeOrder").disabled = count === 0;

                        document.getElementById("cartData").value =
                            ids.map(id => id + ":" + cart[id]).join(",");
                    }

                    document.getElementById("categories").addEventListener("click", event => {
                        const button = event.target.closest("button");
                        if (!button) return;

                        activeCategory = button.dataset.category;
                        document.querySelectorAll(".category-btn").forEach(btn => {
                            btn.classList.toggle("active", btn === button);
                        });
                        filterFood();
                    });

                    document.getElementById("searchInput").addEventListener("input", filterFood);

                    function filterFood() {
                        const query = document.getElementById("searchInput").value.toLowerCase().trim();
                        let visible = 0;

                        document.querySelectorAll(".food-card").forEach(card => {
                            const categoryMatch = activeCategory === "All" ||
                                card.dataset.category === activeCategory;
                            const searchMatch = card.dataset.search.includes(query);
                            const show = categoryMatch && searchMatch;

                            card.style.display = show ? "" : "none";
                            if (show) visible++;
                        });

                        document.getElementById("noResults").style.display =
                            visible === 0 ? "block" : "none";
                    }

                    document.getElementById("cartJump").addEventListener("click", () => {
                        document.getElementById("checkout").scrollIntoView({ behavior: "smooth" });
                    });

                    document.getElementById("checkoutForm").addEventListener("submit", event => {
                        if (Object.keys(cart).length === 0) {
                            event.preventDefault();
                            alert("Please add food to your cart first.");
                            return;
                        }

                        document.getElementById("cartData").value =
                            Object.keys(cart)
                                .filter(id => cart[id] > 0)
                                .map(id => id + ":" + cart[id])
                                .join(",");
                    });

                    renderCart();
                </script>
            </body>
            </html>
            """;

        return html.replace("__CARDS__", cards.toString());
    }

    private static void handleCheckout(HttpExchange exchange) throws IOException {
        String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8
        );

        Map<String, String> form = parseForm(body);
        String name = form.getOrDefault("customerName", "").trim();
        String phone = form.getOrDefault("phone", "").trim();
        String address = form.getOrDefault("address", "").trim();
        String items = form.getOrDefault("items", "");

        if (name.isBlank() || phone.isBlank() || address.isBlank()) {
            sendHtml(exchange, 400,
                    messagePage("Missing details", "Please enter your name, phone number and address."));
            return;
        }

        if (name.length() > 80 || phone.length() > 20 || address.length() > 500) {
            sendHtml(exchange, 400,
                    messagePage("Invalid details", "One or more fields are too long."));
            return;
        }

        Map<Integer, Integer> quantities = parseItems(items);
        if (quantities.isEmpty()) {
            sendHtml(exchange, 400,
                    messagePage("Your cart is empty", "Please return to the menu and add food."));
            return;
        }

        long total = 0;
        for (Map.Entry<Integer, Integer> entry : quantities.entrySet()) {
            Food food = findFood(entry.getKey());

            if (food == null || entry.getValue() < 1 || entry.getValue() > 50) {
                sendHtml(exchange, 400,
                        messagePage("Invalid order", "Please return to the menu and try again."));
                return;
            }

            total += (long) food.price() * entry.getValue();
        }

        int orderId = ORDER_COUNTER.getAndIncrement();
        System.out.println("New demo order #" + orderId + " | Total: ₹" + total);

        StringBuilder summary = new StringBuilder();
        for (Map.Entry<Integer, Integer> entry : quantities.entrySet()) {
            Food food = findFood(entry.getKey());
            summary.append("<li>")
                    .append(escapeHtml(food.name()))
                    .append(" × ").append(entry.getValue())
                    .append(" — ₹").append((long) food.price() * entry.getValue())
                    .append("</li>");
        }

        String html = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <title>Order confirmed | Crave Kitchen</title>
                <style>
                    body {
                        margin: 0;
                        padding: 24px;
                        background: #fffaf5;
                        color: #222;
                        font-family: Arial, sans-serif;
                        display: grid;
                        min-height: 90vh;
                        place-items: center;
                    }
                    .card {
                        background: white;
                        width: 100%;
                        max-width: 520px;
                        padding: 35px;
                        border-radius: 20px;
                        box-shadow: 0 10px 40px #0000000c;
                    }
                    .check { font-size: 48px; color: #237a57; }
                    h1 { margin-bottom: 8px; }
                    .muted { color: #737373; line-height: 1.6; }
                    .summary { padding: 15px 0; line-height: 1.9; }
                    .total {
                        border-top: 1px solid #eee;
                        padding-top: 15px;
                        font-size: 21px;
                        font-weight: bold;
                    }
                    a {
                        display: block;
                        text-align: center;
                        padding: 15px;
                        margin-top: 25px;
                        background: #f06428;
                        color: white;
                        text-decoration: none;
                        border-radius: 10px;
                        font-weight: bold;
                    }
                    .notice {
                        padding: 15px 16px;
                        background: linear-gradient(135deg, #fff0df, #ffe2d1);
                        border: 1px solid #ffd0b5;
                        border-radius: 12px;
                        color: #873b19;
                        font-size: 14px;
                        font-weight: 700;
                        line-height: 1.6;
                    }
                </style>
            </head>
            <body>
                <main class="card">
                    <div class="check">✓</div>
                    <h1>Thank you, __NAME__!</h1>
                    <p class="muted">Your delicious picks are all together. Take one last look at your order summary!</p>
                    <p><strong>Order number:</strong> #__ORDER__</p>
                    <p><strong>Delivery address:</strong><br>__ADDRESS__</p>
                    <ul class="summary">__SUMMARY__</ul>
                    <div class="total">Total: ₹__TOTAL__</div>
                    <p class="notice">
                        🔥 Cravings handled! Your order summary is ready.<br>
                        Great taste starts with a great choice. 🍔✨
                    </p>
                    <a href="/">← Back to Crave Kitchen</a>
                </main>
            </body>
            </html>
            """;

        html = html.replace("__NAME__", escapeHtml(name))
                .replace("__ORDER__", String.valueOf(orderId))
                .replace("__ADDRESS__", escapeHtml(address))
                .replace("__SUMMARY__", summary.toString())
                .replace("__TOTAL__", String.valueOf(total));

        sendHtml(exchange, 200, html);
    }

    private static Map<String, String> parseForm(String body) {
        Map<String, String> result = new LinkedHashMap<>();

        for (String pair : body.split("&")) {
            int separator = pair.indexOf('=');
            String key = separator >= 0 ? pair.substring(0, separator) : pair;
            String value = separator >= 0 ? pair.substring(separator + 1) : "";

            result.put(
                    URLDecoder.decode(key, StandardCharsets.UTF_8),
                    URLDecoder.decode(value, StandardCharsets.UTF_8)
            );
        }

        return result;
    }

    private static Map<Integer, Integer> parseItems(String items) {
        Map<Integer, Integer> result = new LinkedHashMap<>();

        if (items == null || items.isBlank() || items.length() > 2000) {
            return result;
        }

        try {
            for (String item : items.split(",")) {
                String[] parts = item.split(":", -1);
                if (parts.length != 2) return Map.of();

                int id = Integer.parseInt(parts[0]);
                int quantity = Integer.parseInt(parts[1]);

                if (quantity < 1 || quantity > 50 || findFood(id) == null) {
                    return Map.of();
                }

                result.merge(id, quantity, Integer::sum);
            }
        } catch (NumberFormatException e) {
            return Map.of();
        }

        for (int quantity : result.values()) {
            if (quantity > 50) return Map.of();
        }

        return result;
    }

    private static Food findFood(int id) {
        for (Food food : MENU) {
            if (food.id() == id) return food;
        }
        return null;
    }

    private static String messagePage(String title, String message) {
        return """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1">
                <title>Crave Kitchen</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        background: #fffaf5;
                        padding: 40px 20px;
                        color: #222;
                    }
                    main {
                        max-width: 520px;
                        margin: 40px auto;
                        padding: 30px;
                        border-radius: 16px;
                        background: white;
                    }
                    a { color: #f06428; font-weight: bold; }
                </style>
            </head>
            <body>
                <main>
                    <h1>__TITLE__</h1>
                    <p>__MESSAGE__</p>
                    <a href="/">Return to the menu</a>
                </main>
            </body>
            </html>
            """.replace("__TITLE__", escapeHtml(title))
              .replace("__MESSAGE__", escapeHtml(message));
    }

    private static String escapeHtml(String value) {
        if (value == null) return "";

        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private static void sendHtml(
            HttpExchange exchange, int status, String html) throws IOException {

        byte[] response = html.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set(
                "Content-Type", "text/html; charset=UTF-8");
        exchange.getResponseHeaders().set("Cache-Control", "no-store");
        exchange.sendResponseHeaders(status, response.length);

        try (var output = exchange.getResponseBody()) {
            output.write(response);
        }
    }
}
