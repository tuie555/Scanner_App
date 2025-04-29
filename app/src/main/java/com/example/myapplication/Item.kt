import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ItemViewModel(private val repository: ItemRepository) : ViewModel() {

    // สร้าง StateFlow เพื่อเก็บรายการสินค้า
    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> = _items

    // ฟังก์ชันโหลดรายการสินค้า
    fun loadItems() {
        viewModelScope.launch {
            _items.value = repository.getAllItems()
        }
    }

    // ฟังก์ชันเพิ่มสินค้าลงในฐานข้อมูล
    fun addItem(name: String, category: String) {
        viewModelScope.launch {
            val item = Item(name = name, category = category)
            repository.insert(item)
            loadItems()  // โหลดข้อมูลใหม่หลังจากการเพิ่ม
        }
    }

    // ฟังก์ชันลบสินค้า
    fun deleteItem(item: Item) {
        viewModelScope.launch {
            repository.delete(item)
            loadItems()  // โหลดข้อมูลใหม่หลังจากการลบ
        }
    }

    // ฟังก์ชันอัปเดตสินค้า
    fun updateItem(item: Item) {
        viewModelScope.launch {
            repository.update(item)
            loadItems()  // โหลดข้อมูลใหม่หลังจากการอัปเดต
        }
    }
}
