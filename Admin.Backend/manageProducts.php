<?php
session_start();

if (!isset($_SESSION['username'])) {
    header("Location: login.php");
    exit();
}
?>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="icon" type="image/png" href="pcxlogo.png">
    <title>PCX Admin</title>
    <link rel="stylesheet" href="manageProducts.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css">
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script src="js/pageloader.js"></script>
</head>

<body>
    
    
<div class="sidebar">
        <div class="logo">
            <img src="pcxlogo.png" alt="PCX Logo">
        </div>
        <div class="profile">
            <img src="profile.png" alt="Profile Picture" class="profile-pic">
            <div class="profile-info">
            <h2><?php echo htmlspecialchars ($_SESSION['full_name'])?></h2>
                <a href="editProfile.php" class="edit-profile-btn">Edit Profile</a>
            </div>
        </div>

        <ul class="menu">
            <li><a href="homepage.php"><i class="fas fa-home"></i> Dashboard</a></li>
            <a href="manageProducts.php"></a><li class="active"><i class="fas fa-box"></i> Products</li>
            <li><a href="manageOrder.php"><i class="fas fa-shopping-cart"></i> Orders</a></li>
            <li><a href="customer.php"><i class="fas fa-users"></i> Customers</a></li>
            <li><a href="manageUser.php"><i class="fa-solid fa-user"></i> Users</a></li>
            <li><a href="reports.php"><i class="fas fa-chart-bar"></i> Reports</a></li>
            
            <li class="logout">
                <a href="login.php"><i class="fas fa-sign-out-alt"></i> Logout</a>
            </li>
        </ul>
    </div>
    
    <div class="content">
    <div class="animateds">
        <h2 class="animated-text1">Products</h2>

        <div class="filter-container">
            <label for="categoryFilter">Filter by Category:</label>
            <select id="categoryFilter">
                <option value="All">All Categories</option>
                <option value="Processor">Processor</option>
                <option value="Mother Board">Mother Board</option>
                <option value="Graphics Card">Graphics Card</option>
                <option value="Memory">Memory</option>
                <option value="Case">Case</option>
                <option value="Solid State Drive">Solid State Drive</option>
                <option value="Monitor">Monitor</option>
                <option value="Keyboard">Keyboard</option>
                <option value="Mouse">Mouse</option>
                <option value="PC Case">PC Case</option>
                <option value="Laptops">Laptops</option>
            </select>
            <button class="add-product-button">Add Product</button>
        </div>

        <!-- Product Table -->
        <table class="product-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Image</th>
                    <th>Name</th>
                    <th>Description</th>
                    <th>Category</th>
                    <th>Price</th>
                    <th>Stocks</th>
                    <th>Status</th>
                    <th>Actions</th>
                </tr>
            </thead>
            <tbody>
                <!-- Product rows go here -->
            </tbody>
        </table>
        </div>
        </div>


        <!-- Add Product Modal -->
        <div id="addProductModal" class="modal">
            <div id="addProductModalContent" class="modal-content">
                <span class="close">&times;</span>
                <h2>New Product Item</h2>
                <form id="addProductForm">
                    <label for="productName">Product Name:</label>
                    <input type="text" id="productName" name="productName" required><br><br>
                    <label for="productDescription">Description:</label>
                    <input type="text" id="productDescription" name="productDescription" required><br><br>
                    <label for="productPrice">Price:</label>
                    <input type="number" id="productPrice" name="productPrice" required><br><br>
                    <label for="productCategory">Category:</label>
                    <select id="productCategory" name="productCategory">
                    <option value="Processor">Processor</option>
                        <option value="Mother Board">Mother Board</option>
                        <option value="Graphics Card">Graphics Card</option>
                        <option value="Memory">Memory</option>
                        <option value="Case">Case</option>
                        <option value="Solid State Drive">Solid State Drive</option>
                        <option value="Monitor">Monitor</option>
                        <option value="Keyboard">Keyboard</option>
                        <option value="Mouse">Mouse</option>
                        <option value="PC Case">PC Case</option>
                        <option value="Laptops">Laptops</option>
                    </select><br><br>
                    <label for="productStocks">Stocks:</label> <!-- Stocks -->
                    <input type="number" id="productStocks" name="productStocks" required><br><br>
                    <label for="productImage">Choose Image:</label>
                    <input type="file" id="productImage" name="productImage"><br><br>

                    <div class="mb-3">
                            <label for="productStatus" class="form-label">Status</label>
                            <select class="form-control" id="productStatus">
                                <option value="In Stock">In Stock</option>
                                <option value="Low Stock">Low Stock</option>
                                <option value="Out of Stock">Out of Stock</option>
                            </select>
                        </div>
                    <button type="submit">Add Item</button>
                </form>
            </div>
        </div>

        <!-- Edit Product Modal -->
<div id="editProductModal" class="modal">
    <div class="modal-content">
        <span class="close">&times;</span>
        <h2>Edit Product</h2>
        <form id="editProductForm">
            <input type="hidden" id="editProductId" name="editProductId">
            
            <label for="editProductName">Name:</label>
            <input type="text" id="editProductName" name="editProductName" required>
            
            <label for="editProductDescription">Description:</label>
            <textarea id="editProductDescription" name="editProductDescription" required></textarea>
            
            <label for="editProductPrice">Price:</label>
            <input type="number" id="editProductPrice" name="editProductPrice" step="0.01" required>
            
            <label for="editProductCategory">Category:</label>
            <input type="text" id="editProductCategory" name="editProductCategory" required>
            
            <label for="editProductStocks">Stocks:</label>
            <input type="number" id="editProductStocks" name="editProductStocks" required>
            
            <label for="editProductStatus">Status:</label>
            <select id="editProductStatus" name="editProductStatus">
                <option value="active">Active</option>
                <option value="inactive">Inactive</option>
            </select>
            
            <button type="submit" class="save-btn">Save Changes</button>
        </form>
    </div>
</div>

        <script>
    document.addEventListener('DOMContentLoaded', function() {
    let allProducts = []; // Storing products for filtering

    fetch('fetch_products.php')
        .then(response => response.json())
        .then(products => {
            allProducts = products; // store for filtering
            renderProducts(allProducts);
        })
        .catch(error => console.error('Error:', error));

    // Filtering products by category
    document.getElementById('categoryFilter').addEventListener('change', function() {
        const selectedCategory = this.value;
        const filteredProducts = selectedCategory === 'All' ? allProducts
            : allProducts.filter(product => product.category === selectedCategory);

        renderProducts(filteredProducts);
    });
});

function renderProducts(products) {
    const tbody = document.querySelector('.product-table tbody');
    tbody.innerHTML = '';

    products.forEach(product => {
        const newRow = document.createElement('tr');
        newRow.innerHTML = `
            <td>${product.id}</td>
            <td><img src="uploads/${product.image}" alt="" width="50"></td>
            <td><span class="product-name">${product.name}</span></td>
            <td><span class="product-description">${product.description}</span></td>
            <td><span class="product-category">${product.category}</span></td>
            <td><span class="product-price">₱${parseFloat(product.price).toFixed(2)}</span></td>
            <td><span class="product-stocks">${product.stocks}</span></td>
            <td><span class="product-status">${product.status}</span></td>
            <td class="actions">
                <button class="edit-button" data-id="${product.id}"><i class="fas fa-edit"></i></button>
                <button class="delete-button"><i class="fas fa-trash-alt"></i></button>
            </td>
        `;
        tbody.appendChild(newRow);
        addEventListeners(newRow); // Keep your events working!
    });
}


    document.getElementById('addProductForm').addEventListener('submit', function(event) {
        event.preventDefault();

        let formData = new FormData(this);

        fetch('add_product.php', {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert("Product added successfully!");
                document.getElementById('addProductModal').style.display = 'none';
                location.reload(); 
            } else {
                alert("Error: " + data.error);
            }
        })
        .catch(error => console.error('Error:', error));
    });

    function addEventListeners(row) {
        const deleteButton = row.querySelector('.delete-button');
        deleteButton.addEventListener('click', function() {
            const productId = row.querySelector('.edit-button').dataset.id;

            if (confirm('Are you sure you want to delete this product?')) {
                fetch('delete_product.php', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    body: `id=${productId}`
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        alert('Product deleted successfully!');
                        row.remove(); 
                    } else {
                        alert('Delete failed: ' + data.error);
                    }
                })
                .catch(error => console.error('Error:', error));
            }
        });

        const editButton = row.querySelector('.edit-button');
        editButton.addEventListener('click', function() {
            console.log('Edit button clicked'); 
            const productId = editButton.dataset.id;
            const productName = row.querySelector('.product-name').textContent;
            const productDescription = row.querySelector('.product-description').textContent;
            const productCategory = row.querySelector('.product-category').textContent;
            const productPrice = row.querySelector('.product-price').textContent.replace('₱', '');
            const productStocks = row.querySelector('.product-stocks').textContent;
            const productStatus = row.querySelector('.product-status').textContent;

            document.getElementById('editProductName').value = productName;
            document.getElementById('editProductDescription').value = productDescription;
            document.getElementById('editProductCategory').value = productCategory;
            document.getElementById('editProductPrice').value = productPrice;
            document.getElementById('editProductStocks').value = productStocks;
            document.getElementById('editProductStatus').value = productStatus;

            const editProductModal = document.getElementById('editProductModal');
            editProductModal.style.display = 'block';

            const editProductForm = document.getElementById('editProductForm');
            editProductForm.onsubmit = function(event) {
                event.preventDefault();

                const updatedProductName = document.getElementById('editProductName').value;
                const updatedProductDescription = document.getElementById('editProductDescription').value;
                const updatedProductCategory = document.getElementById('editProductCategory').value;
                const updatedProductPrice = document.getElementById('editProductPrice').value;
                const updatedProductStocks = document.getElementById('editProductStocks').value;
                const updatedProductStatus = document.getElementById('editProductStatus').value;

                fetch('edit_product.php', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    body: `id=${productId}&name=${updatedProductName}&description=${updatedProductDescription}&category=${updatedProductCategory}&price=${updatedProductPrice}&stocks=${updatedProductStocks}&status=${updatedProductStatus}`
                })
                .then(response => response.json())
                .then(data => {
                    if (data.success) {
                        alert('Product updated successfully!');
                        row.querySelector('.product-name').textContent = updatedProductName;
                        row.querySelector('.product-description').textContent = updatedProductDescription;
                        row.querySelector('.product-category').textContent = updatedProductCategory;
                        row.querySelector('.product-price').textContent = `₱${parseFloat(updatedProductPrice).toFixed(2)}`;
                        row.querySelector('.product-stocks').textContent = updatedProductStocks;
                        row.querySelector('.product-status').textContent = updatedProductStatus;
                        editProductModal.style.display = 'none';
                    } else {
                        alert('Update failed: ' + data.error);
                    }
                })
                .catch(error => console.error('Error:', error));
            };
        });
    }

    // Add Product button
    const addProductButton = document.querySelector('.add-product-button');
    const addProductModal = document.getElementById('addProductModal');
    const addProductModalClose = document.querySelector('#addProductModal .close');
    const addProductForm = document.getElementById('addProductForm');

    addProductButton.addEventListener('click', function() {
        addProductModal.style.display = 'block';
    });

    // Close the modal
    addProductModalClose.addEventListener('click', function() {
        addProductModal.style.display = 'none';
    });

    // Close the edit modal
    const editProductModalClose = document.querySelector('#editProductModal .close');
    editProductModalClose.addEventListener('click', function() {
        document.getElementById('editProductModal').style.display = 'none';
    });
</script>

    </div>
</body>
</html>