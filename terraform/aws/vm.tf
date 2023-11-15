# ec2
resource "aws_instance" "aws_instance" {
  ami           			= "ami-04341a215040f91bb"
  instance_type 			= "t2.medium"
  vpc_security_group_ids 	= ["${aws_security_group.security_group.id}"]
  associate_public_ip_address = true
  subnet_id = "${aws_subnet.public_subnet.id}"
  key_name = data.aws_key_pair.mcmp_key.key_name
  tags = {
    Name = var.instance_name
  }
}

resource "aws_eip" "eip" {
    instance = aws_instance.aws_instance.id
}

data "aws_key_pair" "mcmp_key" { 
  key_name   = "gcp_key" #aws 서버에 등록된 ssh키 이름
  include_public_key = true
}

variable "instance_name" {
  type    = string
  default = "aws"
}

output "instance_id" {
  value = aws_instance.aws_instance.id
}

output "instance_name" {
  value = aws_instance.aws_instance.tags_all.Name
}

output "instance_status" {
  value = aws_instance.aws_instance.instance_state
}
output "public_ip" {
  value = aws_eip.eip.public_ip
}

output "private_ip" {
  value = aws_instance.aws_instance.private_ip
}

output "memory_type" {
  value = aws_instance.aws_instance.instance_type
}
