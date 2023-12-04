extends Sprite2D

# Called when the node enters the scene tree for the first time.
func _ready():
	position = $"../Board".map_to_local(Vector2i(0, 3))


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(delta):
	rotation += 10 * delta
	# position = get_global_mouse_position()
