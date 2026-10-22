package com.example.myapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.content.res.ColorStateList;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    // ألوان التطبيق
    private static final int GREEN = Color.rgb(22, 163, 106);
    private static final int DARK = Color.rgb(25, 35, 45);
    private static final int BACKGROUND = Color.rgb(246, 248, 250);
    private static final int MUTED = Color.rgb(110, 120, 130);
    private static final int WHITE = Color.WHITE;

    private LinearLayout root;
    private LinearLayout content;
    private LinearLayout header;
    private EditText searchInput;
    private TextView cartBadge;

    private final ArrayList<Product> products = new ArrayList<>();
    private final HashMap<Integer, Integer> cart = new HashMap<>();
    private final ArrayList<Order> orders = new ArrayList<>();

    private int nextOrderId = 1001;

    private final NumberFormat numberFormat =
            NumberFormat.getInstance(new Locale("ar", "SY"));

    // نموذج المنتج
    static class Product {
        int id;
        String name;
        String category;
        String description;
        String emoji;
        int price;

        Product(int id, String name, String category,
                String description, String emoji, int price) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.description = description;
            this.emoji = emoji;
            this.price = price;
        }
    }

    // نموذج الطلب
    static class Order {
        int id;
        String customer;
        String phone;
        String address;
        int total;
        String items;
        String status;

        Order(int id, String customer, String phone,
              String address, int total, String items) {
            this.id = id;
            this.customer = customer;
            this.phone = phone;
            this.address = address;
            this.total = total;
            this.items = items;
            this.status = "قيد المراجعة";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(DARK);
        getWindow().setNavigationBarColor(DARK);

        loadProducts();
        buildApplication();
        showHome();
    }

    // المنتجات الأولية
    private void loadProducts() {
        products.add(new Product(
                1, "برغر لحم", "وجبات",
                "برغر لحم مع الجبن والخضار",
                "🍔", 35000));

        products.add(new Product(
                2, "بيتزا", "وجبات",
                "بيتزا بالجبن والخضار",
                "🍕", 45000));

        products.add(new Product(
                3, "شاورما دجاج", "وجبات",
                "شاورما دجاج مع الثوم والمخلل",
                "🌯", 25000));

        products.add(new Product(
                4, "بطاطا مقلية", "وجبات",
                "بطاطا مقرمشة",
                "🍟", 12000));

        products.add(new Product(
                5, "عصير برتقال", "مشروبات",
                "عصير برتقال منعش",
                "🍊", 10000));

        products.add(new Product(
                6, "مياه معدنية", "مشروبات",
                "عبوة مياه معدنية",
                "💧", 3000));

        products.add(new Product(
                7, "قهوة", "مشروبات",
                "قهوة ساخنة",
                "☕", 8000));

        products.add(new Product(
                8, "كيك شوكولا", "حلويات",
                "قطعة كيك بالشوكولا",
                "🍰", 15000));
    }

    private void buildApplication() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        root.setBackgroundColor(BACKGROUND);

        setContentView(root);

        // رأس التطبيق
        header = new LinearLayout(this);
        header.setOrientation(LinearLayout.VERTICAL);
        header.setPadding(dp(18), dp(16), dp(18), dp(16));
        header.setBackgroundColor(DARK);

        root.addView(header, new LinearLayout.LayoutParams(
                -1, -2));

        TextView brand = text(
                "🚚 وصّلني",
                27, WHITE, true);

        header.addView(brand);

        TextView subtitle = text(
                "طلباتك تصل إليك بسهولة",
                14, Color.LTGRAY, false);

        LinearLayout.LayoutParams subParams =
                new LinearLayout.LayoutParams(-1, -2);

        subParams.topMargin = dp(5);
        header.addView(subtitle, subParams);

        // البحث
        searchInput = new EditText(this);
        searchInput.setSingleLine(true);
        searchInput.setTextSize(15);
        searchInput.setTextColor(DARK);
        searchInput.setHintTextColor(MUTED);
        searchInput.setHint("ابحث عن وجبتك المفضلة...");
        searchInput.setPadding(dp(14), dp(10), dp(14), dp(10));
        searchInput.setInputType(InputType.TYPE_CLASS_TEXT);
        searchInput.setBackground(roundDrawable(WHITE, 14));

        LinearLayout.LayoutParams searchParams =
                new LinearLayout.LayoutParams(-1, dp(48));

        searchParams.topMargin = dp(15);
        header.addView(searchInput, searchParams);

        // شريط التنقل
        LinearLayout nav = new LinearLayout(this);
        nav.setGravity(Gravity.CENTER_VERTICAL);
        nav.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams navParams =
                new LinearLayout.LayoutParams(-1, -2);

        navParams.topMargin = dp(12);
        header.addView(nav, navParams);

        Button homeButton = button("الرئيسية", GREEN, WHITE);
        Button cartButton = button("🛒 السلة", WHITE, DARK);
        Button ordersButton = button("طلباتي", WHITE, DARK);

        nav.addView(homeButton, weightedParams());
        nav.addView(cartButton, weightedParams());
        nav.addView(ordersButton, weightedParams());

        cartBadge = text("", 12, WHITE, true);

        homeButton.setOnClickListener(v -> showHome());

        cartButton.setOnClickListener(v -> showCart());

        ordersButton.setOnClickListener(v -> showOrders());

        // مساحة المحتوى القابلة للتمرير
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);

        root.addView(scroll, new LinearLayout.LayoutParams(
                -1, 0, 1));

        content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setPadding(dp(16), dp(16), dp(16), dp(24));
        content.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        scroll.addView(content);

        // البحث المباشر
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                renderProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private LinearLayout.LayoutParams weightedParams() {
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(0, dp(42), 1);

        params.setMargins(dp(3), 0, dp(3), 0);
        return params;
    }

    // الصفحة الرئيسية
    private void showHome() {
        searchInput.setVisibility(View.VISIBLE);
        searchInput.setText("");

        content.removeAllViews();

        TextView welcome = text(
                "أهلاً بك 👋",
                23, DARK, true);

        content.addView(welcome);

        TextView info = text(
                "اختر ما تحب وسنساعدك في طلبه",
                14, MUTED, false);

        LinearLayout.LayoutParams infoParams =
                new LinearLayout.LayoutParams(-1, -2);

        infoParams.topMargin = dp(5);
        content.addView(info, infoParams);

        LinearLayout.LayoutParams titleParams =
                new LinearLayout.LayoutParams(-1, -2);

        titleParams.topMargin = dp(22);

        TextView section = text(
                "🍽️ قائمة المنتجات",
                19, DARK, true);

        content.addView(section, titleParams);

        renderProducts("");
        updateCartBadge();
    }

    private void renderProducts(String query) {
        // إزالة بطاقات المنتجات فقط
        int startIndex = 3;

        while (content.getChildCount() > startIndex) {
            content.removeViewAt(startIndex);
        }

        String q = query.trim().toLowerCase(
                new Locale("ar", "SY"));

        int found = 0;

        for (Product p : products) {
            if (!p.name.toLowerCase(
                    new Locale("ar", "SY")).contains(q)
                    && !p.category.toLowerCase(
                    new Locale("ar", "SY")).contains(q)
                    && !p.description.toLowerCase(
                    new Locale("ar", "SY")).contains(q)) {
                continue;
            }

            found++;
            addProductCard(p);
        }

        if (found == 0) {
            TextView empty = text(
                    "لا توجد منتجات مطابقة للبحث.",
                    15, MUTED, false);

            LinearLayout.LayoutParams ep =
                    new LinearLayout.LayoutParams(-1, -2);

            ep.topMargin = dp(20);
            content.addView(empty, ep);
        }
    }

    // بطاقة المنتج
    private void addProductCard(Product p) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(15), dp(14), dp(15), dp(14));
        card.setBackground(roundDrawable(WHITE, 16));
        card.setElevation(dp(2));

        LinearLayout.LayoutParams cardParams =
                new LinearLayout.LayoutParams(-1, -2);

        cardParams.topMargin = dp(12);
        content.addView(card, cardParams);

        LinearLayout top = new LinearLayout(this);
        top.setOrientation(LinearLayout.HORIZONTAL);
        top.setGravity(Gravity.CENTER_VERTICAL);

        card.addView(top);

        TextView emoji = text(p.emoji, 40, DARK, false);

        LinearLayout.LayoutParams emojiParams =
                new LinearLayout.LayoutParams(dp(65), dp(65));

        emojiParams.setMargins(0, 0, dp(12), 0);
        emoji.setGravity(Gravity.CENTER);
        emoji.setBackground(roundDrawable(
                Color.rgb(240, 245, 241), 14));

        top.addView(emoji, emojiParams);

        LinearLayout details = new LinearLayout(this);
        details.setOrientation(LinearLayout.VERTICAL);

        top.addView(details, new LinearLayout.LayoutParams(
                0, -2, 1));

        details.addView(text(p.name, 17, DARK, true));

        TextView description = text(
                p.description, 12, MUTED, false);

        LinearLayout.LayoutParams descParams =
                new LinearLayout.LayoutParams(-1, -2);

        descParams.topMargin = dp(5);
        details.addView(description, descParams);

        TextView category = text(
                "التصنيف: " + p.category,
                12, GREEN, true);

        LinearLayout.LayoutParams catParams =
                new LinearLayout.LayoutParams(-1, -2);

        catParams.topMargin = dp(5);
        details.addView(category, catParams);

        LinearLayout bottom = new LinearLayout(this);
        bottom.setGravity(Gravity.CENTER_VERTICAL);
        bottom.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams bottomParams =
                new LinearLayout.LayoutParams(-1, -2);

        bottomParams.topMargin = dp(14);
        card.addView(bottom, bottomParams);

        TextView price = text(
                money(p.price) + " ل.س",
                15, GREEN, true);

        bottom.addView(price, new LinearLayout.LayoutParams(
                0, -2, 1));

        Button add = button("+ إضافة للسلة", GREEN, WHITE);

        bottom.addView(add, new LinearLayout.LayoutParams(
                -2, dp(42)));

        add.setOnClickListener(v -> {
            int current = cart.containsKey(p.id)
                    ? cart.get(p.id) : 0;

            cart.put(p.id, current + 1);
            updateCartBadge();

            Toast.makeText(
                    this,
                    "تمت إضافة " + p.name + " إلى السلة",
                    Toast.LENGTH_SHORT
            ).show();
        });
    }

    // صفحة السلة
    private void showCart() {
        searchInput.setVisibility(View.GONE);
        content.removeAllViews();

        content.addView(text(
                "🛒 سلة المشتريات",
                23, DARK, true));

        if (cart.isEmpty()) {
            addSpace(content, 20);

            content.addView(text(
                    "سلتك فارغة حالياً.",
                    16, MUTED, false));

            addSpace(content, 15);

            Button back = button(
                    "تصفح المنتجات", GREEN, WHITE);

            content.addView(back);
            back.setOnClickListener(v -> showHome());

            return;
        }

        for (Product p : products) {
            if (!cart.containsKey(p.id)) continue;

            int qty = cart.get(p.id);

            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(14), dp(12), dp(14), dp(12));
            card.setBackground(roundDrawable(WHITE, 14));

            LinearLayout.LayoutParams cp =
                    new LinearLayout.LayoutParams(-1, -2);

            cp.topMargin = dp(10);
            content.addView(card, cp);

            card.addView(text(
                    p.emoji + "  " + p.name,
                    17, DARK, true));

            TextView price = text(
                    "سعر القطعة: " + money(p.price) + " ل.س",
                    13, MUTED, false);

            LinearLayout.LayoutParams pp =
                    new LinearLayout.LayoutParams(-1, -2);

            pp.topMargin = dp(5);
            card.addView(price, pp);

            LinearLayout row = new LinearLayout(this);
            row.setGravity(Gravity.CENTER_VERTICAL);

            LinearLayout.LayoutParams rp =
                    new LinearLayout.LayoutParams(-1, -2);

            rp.topMargin = dp(10);
            card.addView(row, rp);

            Button minus = button("−", DARK, WHITE);
            Button plus = button("+", GREEN, WHITE);
            Button remove = button("حذف", Color.rgb(190, 55, 55), WHITE);

            row.addView(minus, new LinearLayout.LayoutParams(
                    dp(45), dp(40)));

            TextView quantity = text(
                    "  " + qty + "  ", 16, DARK, true);

            quantity.setGravity(Gravity.CENTER);
            row.addView(quantity, new LinearLayout.LayoutParams(
                    0, -2, 1));

            row.addView(plus, new LinearLayout.LayoutParams(
                    dp(45), dp(40)));

            LinearLayout.LayoutParams removeParams =
                    new LinearLayout.LayoutParams(-2, dp(40));

            removeParams.setMargins(dp(8), 0, 0, 0);
            row.addView(remove, removeParams);

            minus.setOnClickListener(v -> {
                int n = cart.get(p.id) - 1;

                if (n <= 0) {
                    cart.remove(p.id);
                } else {
                    cart.put(p.id, n);
                }

                updateCartBadge();
                showCart();
            });

            plus.setOnClickListener(v -> {
                cart.put(p.id, cart.get(p.id) + 1);
                updateCartBadge();
                showCart();
            });

            remove.setOnClickListener(v -> {
                cart.remove(p.id);
                updateCartBadge();
                showCart();
            });
        }

        addSpace(content, 18);

        TextView total = text(
                "الإجمالي: " + money(cartTotal()) + " ل.س",
                21, GREEN, true);

        content.addView(total);

        addSpace(content, 15);

        Button checkout = button(
                "متابعة وإتمام الطلب", GREEN, WHITE);

        content.addView(checkout,
                new LinearLayout.LayoutParams(-1, dp(50)));

        checkout.setOnClickListener(v -> showCheckout());
    }

    // إتمام الطلب
    private void showCheckout() {
        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(dp(5), dp(5), dp(5), dp(5));

        EditText name = input("الاسم الكامل");
        EditText phone = input("رقم الهاتف");
        EditText address = input("عنوان التوصيل بالتفصيل");

        phone.setInputType(InputType.TYPE_CLASS_PHONE);
        address.setMinLines(2);
        address.setGravity(Gravity.TOP | Gravity.RIGHT);

        form.addView(name);
        addSpace(form, 10);
        form.addView(phone);
        addSpace(form, 10);
        form.addView(address);

        TextView amount = text(
                "المبلغ المطلوب: " + money(cartTotal()) + " ل.س",
                16, GREEN, true);

        addSpace(form, 12);
        form.addView(amount);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("تأكيد بيانات التوصيل")
                .setView(form)
                .setNegativeButton("إلغاء", null)
                .setPositiveButton("تأكيد الطلب", null)
                .create();

        dialog.setOnShowListener(d -> {
            Button confirm = dialog.getButton(
                    AlertDialog.BUTTON_POSITIVE);

            confirm.setOnClickListener(v -> {
                String customer = name.getText().toString().trim();
                String mobile = phone.getText().toString().trim();
                String location = address.getText().toString().trim();

                if (customer.isEmpty()
                        || mobile.isEmpty()
                        || location.isEmpty()) {

                    Toast.makeText(
                            this,
                            "يرجى تعبئة جميع الحقول",
                            Toast.LENGTH_SHORT
                    ).show();

                    return;
                }

                if (mobile.length() < 7) {
                    phone.setError("أدخل رقم هاتف صحيحاً");
                    return;
                }

                if (cart.isEmpty()) {
                    dialog.dismiss();
                    showCart();
                    return;
                }

                int total = cartTotal();
                String items = buildOrderItems();

                Order order = new Order(
                        nextOrderId++,
                        customer,
                        mobile,
                        location,
                        total,
                        items
                );

                orders.add(0, order);

                cart.clear();
                updateCartBadge();

                dialog.dismiss();

                new AlertDialog.Builder(this)
                        .setTitle("تم إنشاء الطلب بنجاح ✅")
                        .setMessage(
                                "رقم الطلب: #" + order.id
                                + "\nالاسم: " + order.customer
                                + "\nالإجمالي: " + money(order.total)
                                + " ل.س"
                                + "\n\nحالة الطلب: "
                                + order.status
                        )
                        .setPositiveButton("عرض طلباتي",
                                (dd, which) -> showOrders())
                        .setNeutralButton("الرئيسية",
                                (dd, which) -> showHome())
                        .show();
            });
        });

        dialog.show();
    }

    // صفحة طلباتي
    private void showOrders() {
        searchInput.setVisibility(View.GONE);
        content.removeAllViews();

        content.addView(text(
                "📋 طلباتي",
                23, DARK, true));

        if (orders.isEmpty()) {
            addSpace(content, 20);

            content.addView(text(
                    "لم تقم بإنشاء أي طلب حتى الآن.",
                    15, MUTED, false));

            addSpace(content, 15);

            Button browse = button(
                    "ابدأ التسوق", GREEN, WHITE);

            content.addView(browse);
            browse.setOnClickListener(v -> showHome());

            return;
        }

        for (Order order : orders) {
            LinearLayout card = new LinearLayout(this);
            card.setOrientation(LinearLayout.VERTICAL);
            card.setPadding(dp(15), dp(14), dp(15), dp(14));
            card.setBackground(roundDrawable(WHITE, 14));

            LinearLayout.LayoutParams cp =
                    new LinearLayout.LayoutParams(-1, -2);

            cp.topMargin = dp(12);
            content.addView(card, cp);

            card.addView(text(
                    "طلب رقم #" + order.id,
                    18, DARK, true));

            addSpace(card, 8);

            card.addView(text(
                    "العميل: " + order.customer,
                    14, DARK, false));

            addSpace(card, 5);

            card.addView(text(
                    "الهاتف: " + order.phone,
                    14, DARK, false));

            addSpace(card, 5);

            card.addView(text(
                    "العنوان: " + order.address,
                    14, DARK, false));

            addSpace(card, 8);

            card.addView(text(
                    "المنتجات:\n" + order.items,
                    14, DARK, false));

            addSpace(card, 8);

            card.addView(text(
                    "الإجمالي: " + money(order.total) + " ل.س",
                    16, GREEN, true));

            addSpace(card, 6);

            card.addView(text(
                    "الحالة: " + order.status,
                    14, MUTED, true));
        }

        addSpace(content, 15);

        Button shop = button(
                "طلب جديد", GREEN, WHITE);

        content.addView(shop);
        shop.setOnClickListener(v -> showHome());
    }

    private String buildOrderItems() {
        StringBuilder result = new StringBuilder();

        for (Product p : products) {
            if (cart.containsKey(p.id)) {
                result.append(p.name)
                        .append(" × ")
                        .append(cart.get(p.id))
                        .append(" = ")
                        .append(money(
                                p.price * cart.get(p.id)))
                        .append(" ل.س\n");
            }
        }

        return result.toString().trim();
    }

    private int cartTotal() {
        int total = 0;

        for (Product p : products) {
            if (cart.containsKey(p.id)) {
                total += p.price * cart.get(p.id);
            }
        }

        return total;
    }

    private void updateCartBadge() {
        int count = 0;

        for (int qty : cart.values()) {
            count += qty;
        }

        if (cartBadge != null) {
            cartBadge.setText(
                    count > 0 ? "في السلة: " + count : "");
        }
    }

    private String money(int value) {
        return numberFormat.format(value);
    }

    // عناصر واجهة مساعدة
    private TextView text(
            String value, float size, int color, boolean bold) {

        TextView tv = new TextView(this);
        tv.setText(value);
        tv.setTextSize(size);
        tv.setTextColor(color);
        tv.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        tv.setTextDirection(View.TEXT_DIRECTION_RTL);

        if (bold) {
            tv.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        }

        return tv;
    }

    private Button button(
            String label, int background, int foreground) {

        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(13);
        b.setTextColor(foreground);
        b.setAllCaps(false);
        b.setPadding(dp(8), 0, dp(8), 0);
        b.setMinHeight(0);
        b.setMinimumHeight(0);
        b.setStateListAnimator(null);

        b.setBackground(roundDrawable(background, 12));

        return b;
    }

    private EditText input(String hint) {
        EditText e = new EditText(this);
        e.setHint(hint);
        e.setSingleLine(false);
        e.setTextSize(15);
        e.setTextColor(DARK);
        e.setHintTextColor(MUTED);