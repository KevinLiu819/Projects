extends TileMap

const PIECE_TYPE_MAP = {0: "B", 2: "N", 4: "Q", 5: "R"}

var color : bool

signal selected_promotion(type)

# Called when the node enters the scene tree for the first time.
func _ready():
	if not color:
		for square in get_used_cells(0):
			var atlas_coords = get_cell_atlas_coords(1, square)
			set_cell(1, square, 1, atlas_coords - Vector2i(0, 1))

func _input(event):
	if event.is_action_pressed("click"):
		var mouse_square = local_to_map(get_local_mouse_position())
		if mouse_square in get_used_cells(0):
			selected_promotion.emit(PIECE_TYPE_MAP[get_cell_atlas_coords(1, mouse_square).x])
		else:
			selected_promotion.emit("")
