extends Sprite2D

const WIDTH = 141
const HEIGHT = 128
const PIECE_TYPE = "BKNPQR"

var square : Vector2i
var type : String
var color : bool
var valid_moves : Array

var moves = 0
var follow_mouse = false

# Called when the node enters the scene tree for the first time.
func _ready():
	position = get_parent().map_to_local(square)

# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(_delta):
	region_rect = Rect2i(PIECE_TYPE.find(type) * WIDTH, int(color) * HEIGHT, WIDTH, HEIGHT)
	if Input.is_action_pressed("click") and follow_mouse:
		position = get_global_mouse_position()

func _input(event):
	var mouse_pos = get_global_mouse_position()
	var mouse_square = get_parent().local_to_map(mouse_pos)
	if event.is_action_pressed("click") \
			and mouse_square == square and get_parent().white_to_move == color:
		print(mouse_square)
		follow_mouse = true
		valid_moves = get_parent().find_valid_moves(self, true)
		z_index = 1
		print(valid_moves)
	elif event.is_action_released("click") and follow_mouse:
		print(mouse_square)
		follow_mouse = false
		z_index = 0
		if valid_moves.has(mouse_square):
			get_parent().make_move(self, mouse_square, true)
		else:
			position = get_parent().map_to_local(square)
