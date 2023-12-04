extends Control


# Called when the node enters the scene tree for the first time.
func _ready():
	pass # Replace with function body.


# Called every frame. 'delta' is the elapsed time since the previous frame.
func _process(_delta):
	pass


func _on_generate_move_label(move_text):
	if $VBoxContainer/MovesContainer.get_child_count() % 3 == 0:
		var move_num_label = Label.new()
		move_num_label.text = str($VBoxContainer/MovesContainer.get_child_count() / 3)
		move_num_label.horizontal_alignment = HORIZONTAL_ALIGNMENT_CENTER
		$VBoxContainer/MovesContainer.add_child(move_num_label)
	var move_label = Label.new()
	move_label.text = move_text
	move_label.horizontal_alignment = HORIZONTAL_ALIGNMENT_CENTER
	$VBoxContainer/MovesContainer.add_child(move_label)

