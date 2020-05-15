package com.example

import com.example.data.Database
import com.fasterxml.jackson.databind.ObjectMapper
import graphql.schema.idl.RuntimeWiring.newRuntimeWiring
import graphql.schema.idl.SchemaGenerator
import graphql.schema.idl.SchemaParser

private val schemaDef = SchemaParser().parse("""
 
type Query {
    blockById(id: String): Block    
}
    
type BlockType {
    block: Block    
}
    
type Data {
  from: String
  to: String
  quantity: String
  memo: String
}

type Authorization {
  actor: String
  permission: String
}

type Actions {
  account: String
  name: String
  hex_data: String
  data: Data
  authorization: [Authorization]
}

type Transaction {
  expiration: String
  ref_block_num: Int
  ref_block_prefix: Int
  max_net_usage_words: Int
  max_cpu_usage_ms: Int
  delay_sec: Int
  actions: [Actions]
  context_free_actions: [String]
}

type Trx {
  id: String
  compression: String
  packed_context_free_data: String
  packed_trx: String
  transaction: Transaction
  context_free_data: [String]
  signatures: [String]
}

type Transactions {
  status: String
  cpu_usage_us: Int
  net_usage_words: Int
  trx: Trx
}

type Producers {
  producer_name: String
  block_signing_key: String
}

type NewProducers {
  version: Int
  producers: [Producers]
}

type Block {
  id: String
  timestamp: String
  producer: String
  confirmed: Int
  previous: String
  transaction_mroot: String
  action_mroot: String
  schedule_version: Int
  producer_signature: String
  block_num: Int
  ref_block_prefix: Int
  transactions: [Transactions]
  new_producers: NewProducers
}

type Chain {
  server_version: String
  chain_id: String
  head_block_num: Int
  last_irreversible_block_num: Int
  last_irreversible_block_id: String
  head_block_id: String
  head_block_time: String
  head_block_producer: String
  virtual_block_cpu_limit: Int
  virtual_block_net_limit: Int
  block_cpu_limit: Int
  block_net_limit: Int
  server_version_string: String
  fork_db_head_block_num: Int
  fork_db_head_block_id: String
  server_full_version_string: String
}

schema {
    query: Query
}
""")

var runtimeWiring = newRuntimeWiring()
        .type("Query") { builder ->
            builder.dataFetcher("blockById") { env ->
                val id = env.getArgument<String>("id")
                val block = Database.getBlockById(id)
                ObjectMapper().readValue(block, HashMap::class.java)
            }
        }
        .build()

var graphQLSchema = SchemaGenerator().makeExecutableSchema(schemaDef, runtimeWiring)