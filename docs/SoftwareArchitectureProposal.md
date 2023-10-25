### Software Architecture Proposal

###### Front-end and back-end communication

The front-end and back-end communicate using the JSON format, and the key-value pairs of each JSON object need to be determined jointly by the front-end and back-end developers receiving the object. There are some alternatives:

- <u>Send the entire scene</u>. Each time an action is taken (explained in detail later), the data representation of the entire scene's state is transmitted to the back-end:
  - Advantages: Only need to model the scene once, and it is universal for front-end and back-end. Implementation is relatively simple. The back-end does not need to do anything, just overwrite the currently saved scene state.
  - Disadvantages:
    - Network latency. A client triggering an action repeatedly sends data to the server, and the server also needs to send it to all front-end developers working in the same project. The problem mentioned at the meeting is more difficult to solve (of course, it can be solved by brute force).
    - Real version control cannot be implemented because the storage will overflow. Of course, if you really adopt @Yongcheng's suggestion to only go back three steps, this problem will not be considered.
- <u>Send account actions</u>: Represent an action as an object and send it to the back-end to perform the same operation.
  - Advantages:
    - Small network latency.
    - Better version implementation (ignore this for now).
  - Disadvantages:
    - Need to model the action, that is, you need to write **`encode_action`** and **`decode_action`** operations to turn the action into a data model and send it to the back-end. The back-end also needs to establish corresponding data operations. (It's complicated!)



###### In memory data storage

This depends on the scenario assumptions and there are several alternatives that could be considered:

- Only exists in the front-end. This approach should be simple because all graphics calculations are completed by the graphics library and the front-end and back-end communication protocol will become simple. However, there are several problems:
  - <u>Unable to implement multiple account synchronisation</u>: Because we want to achieve collaboration among multiple clients, at any point in time, whenever a client accesses this project, the scene they see must be exactly the same (for the data description of the scene). If we only store the scene data in the front-end, assuming that users A and B complete their work at the same time t0, their scene data is consistent. Then, if account A starts working at t1 but B does not, when the two decide to start working again at time t2, the scene descriptions they see are different.
  - <u>Extreme assumption dependency:</u> The client side might experience failure that the data would be lost forever. 
- Back-end stores data, but there are several implementation challenges:
  - How to represent these data on the back-end (from basic geometric shapes to specific doors and windows), and there are several ways:
    - The back-end also uses WebGL. The back-end and front-end use the same expression calculation mode.
      - <u>Advantages</u>: Simple, the front-end and back-end code is consistent.
      - <u>Disadvantages</u>: Performance, and for the back-end, is it really necessary to represent these geometric shapes?
    - Custom data model and operation on the back-end:
      - <u>Advantages</u>: Performance can be optimized.
      - <u>Disadvantages</u>: Requires a lot of time to model and write code.

