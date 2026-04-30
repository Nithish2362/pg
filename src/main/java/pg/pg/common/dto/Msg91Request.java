package pg.pg.common.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Msg91Request {
    private String flow_id;
    private String sender;
    private String mobiles;
    private String VAR1;
    private String VAR2;
}
