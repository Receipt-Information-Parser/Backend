package cloudComputing.ReceiptMate.user.dto;

import cloudComputing.ReceiptMate.user.dto.response.UserResponse;
import cloudComputing.ReceiptMate.user.dto.request.SignUpRequest;
import cloudComputing.ReceiptMate.user.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {


    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    User requestToUser(SignUpRequest signUpRequest);

    UserResponse userToResponse(User user);
}
